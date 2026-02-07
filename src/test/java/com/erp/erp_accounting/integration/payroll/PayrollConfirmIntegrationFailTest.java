package com.erp.erp_accounting.integration.payroll;

import com.erp.erp_accounting.accounting.account.service.AccountService;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.AutoVoucherService;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.fixture.EmployeeIntegrationFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmService;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(PayrollConfirmIntegrationFailTest.AutoVoucherFailTestConfig.class)
@DisplayName("급여 확정 + 자동분개 통합 테스트 (실패)")
class PayrollConfirmIntegrationFailTest {

    @Autowired
    private PayrollConfirmService payrollConfirmService;

    @Autowired
    private PayrollConfirmRepository payrollConfirmRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private User hrUser;
    private final YearMonth PAY_MONTH = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        hrUser = userRepository.findByUsername("hr").orElseThrow();
        Employee employee = EmployeeIntegrationFixture.saveEmployee(employeeRepository, userRepository);

        Payroll payroll = PayrollFixture.calculatedPayroll(employee);
        payrollRepository.save(payroll);
    }

    @Test
    @DisplayName("자동분개 생성 중 예외 발생 시 급여 확정 롤백")
    void confirmPayroll_rollback_when_auto_voucher_fails() {
        // given
        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);

        // when & then
        assertThatThrownBy(() -> payrollConfirmService.confirm(confirmId, hrUser))
                .isInstanceOf(RuntimeException.class);

        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        // 롤백 검증
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CREATED);
    }

    @TestConfiguration
    static class AutoVoucherFailTestConfig {

        @Bean
        @Primary
        public AutoVoucherService autoVoucherServiceFail(
                VoucherRepository voucherRepository,
                VoucherService voucherService,
                AccountService accountService,
                AccountingPeriodService accountingPeriodService
        ) {
            return new AutoVoucherService(
                    voucherRepository,
                    voucherService,
                    accountService,
                    accountingPeriodService
            ) {
                @Override
                public void createFromPayrollConfirm(PayrollConfirm confirm) {
                    throw new RuntimeException("AUTO VOUCHER FAIL");
                }
            };
        }
    }
}
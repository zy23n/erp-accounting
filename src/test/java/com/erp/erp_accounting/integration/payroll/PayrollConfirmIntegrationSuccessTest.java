package com.erp.erp_accounting.integration.payroll;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.*;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("급여 확정 + 자동분개 통합 테스트 (성공)")
class PayrollConfirmIntegrationSuccessTest {

    @Autowired
    private PayrollConfirmService payrollConfirmService;

    @Autowired
    private PayrollConfirmRepository payrollConfirmRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountingPeriodService accountingPeriodService;

    private User hrUser;
    private final YearMonth PAY_MONTH = YearMonth.of(2026, 1);
    private final YearMonth PERIOD = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        hrUser = userRepository.findByUsername("hr").orElseThrow();
        Employee employee = EmployeeIntegrationFixture.saveEmployee(employeeRepository, userRepository);

        Payroll payroll = PayrollFixture.calculatedPayroll(employee);
        payrollRepository.save(payroll);
    }

    @Test
    @DisplayName("급여 확정 시 자동분개 전표 생성 + 자동 승인")
    void confirm_success_and_auto_voucher_created() {
        // given
        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);

        // when
        payrollConfirmService.confirm(confirmId, hrUser);

        // then - 급여 확정 상태
        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CONFIRMED);
        assertThat(confirm.getConfirmedBy().getId()).isEqualTo(hrUser.getId());
        assertThat(confirm.getConfirmedAt()).isNotNull();

        // then - 자동 전표 생성
        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId);

        assertThat(vouchers).hasSize(1);
        assertThat(vouchers).allSatisfy(v -> {
            assertThat(v.getSourceType()).isEqualTo(SourceType.PAYROLL);
            assertThat(v.getStatus()).isEqualTo(VoucherStatus.APPROVED);
        });
    }

    @Test
    @DisplayName("급여 확정 취소 시 자동분개 전표 취소")
    void cancelPayrollConfirm_cancelAutoVoucher() {
        // given
        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);
        payrollConfirmService.confirm(confirmId, hrUser);

        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId);

        assertThat(vouchers).hasSize(1);
        assertThat(vouchers.get(0).getStatus()).isEqualTo(VoucherStatus.APPROVED);

        // when - 급여 확정 취소
        payrollConfirmService.cancel(confirmId, hrUser);

        // then - 급여 확정 상태
        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CANCELED);
        assertThat(confirm.getCanceledBy().getId()).isEqualTo(hrUser.getId());
        assertThat(confirm.getCanceledAt()).isNotNull();

        // then - 자동 전표 취소
        Voucher canceledVoucher = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId).get(0);

        assertThat(canceledVoucher.getStatus()).isEqualTo(VoucherStatus.CANCELED);
    }

    @Test
    @DisplayName("회계기간 마감 시 급여 확정 실패")
    void confirmPayroll_fail_when_period_closed() {
        // given
        User adminUser = userRepository.findByUsername("admin").orElseThrow();

        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);

        accountingPeriodService.close(PERIOD, adminUser);

        // when & then
        assertThatThrownBy(() -> payrollConfirmService.confirm(confirmId, hrUser))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PERIOD_ALREADY_CLOSED);
                });

        // 급여 확정 상태 유지
        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CREATED);
        assertThat(confirm.getConfirmedAt()).isNull();
    }
}
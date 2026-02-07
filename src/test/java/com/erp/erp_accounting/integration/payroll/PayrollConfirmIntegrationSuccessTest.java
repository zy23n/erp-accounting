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

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(PayrollConfirmStatus.CONFIRMED, confirm.getStatus());
        assertEquals(hrUser.getId(), confirm.getConfirmedBy().getId());
        assertNotNull(confirm.getConfirmedAt());

        // then - 자동 전표 생성
        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId);

        assertEquals(1, vouchers.size());
        assertEquals(SourceType.PAYROLL, vouchers.get(0).getSourceType());
        assertTrue(vouchers.stream().allMatch(v -> v.getStatus() == VoucherStatus.APPROVED));
    }

    @Test
    @DisplayName("급여 확정 취소 시 자동분개 전표 취소")
    void cancelPayrollConfirm_cancelAutoVoucher() {
        // given
        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);
        payrollConfirmService.confirm(confirmId, hrUser);

        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId);

        assertEquals(1, vouchers.size());
        assertEquals(VoucherStatus.APPROVED, vouchers.get(0).getStatus());

        // when - 급여 확정 취소
        payrollConfirmService.cancel(confirmId, hrUser);

        // then - 급여 확정 상태
        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        assertEquals(PayrollConfirmStatus.CANCELED, confirm.getStatus());
        assertNotNull(confirm.getCanceledAt());
        assertEquals(hrUser.getId(), confirm.getCanceledBy().getId());

        // then - 자동 전표 취소
        Voucher canceledVoucher = voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirmId).get(0);

        assertEquals(VoucherStatus.CANCELED, canceledVoucher.getStatus());
    }

    @Test
    @DisplayName("회계기간 마감 시 급여 확정 실패")
    void confirmPayroll_fail_when_period_closed() {
        // given
        User adminUser = userRepository.findByUsername("admin").orElseThrow();

        Long confirmId = payrollConfirmService.createConfirm(PAY_MONTH);

        accountingPeriodService.close(PERIOD, adminUser);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () ->
                payrollConfirmService.confirm(confirmId, hrUser));

        assertEquals(ErrorCode.PERIOD_ALREADY_CLOSED, ex.getErrorCode());

        // 급여 확정 상태 유지
        PayrollConfirm confirm = payrollConfirmRepository.findById(confirmId).orElseThrow();

        assertEquals(PayrollConfirmStatus.CREATED, confirm.getStatus());
        assertNull(confirm.getConfirmedAt());
    }
}
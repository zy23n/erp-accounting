package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.service.AutoVoucherService;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollConfirmFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("급여 확정 서비스 테스트")
class PayrollConfirmServiceTest {

    @InjectMocks
    private PayrollConfirmService payrollConfirmService;

    @Mock
    private PayrollConfirmRepository payrollConfirmRepository;
    @Mock
    private PayrollRepository payrollRepository;
    @Mock
    private AutoVoucherService autoVoucherService;
    @Mock
    private AccountingPeriodService accountingPeriodService;

    private User confirmer;
    private Payroll payroll;
    private static final YearMonth PAY_MONTH = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        confirmer = UserFixture.hrUser();
        ReflectionTestUtils.setField(confirmer, "id", 10L);

        Employee employee = EmployeeFixture.employee();
        payroll = PayrollFixture.calculatedPayroll(employee);
    }

    @Test
    @DisplayName("급여 확정 처리 성공 + 자동분개 호출")
    void confirm_success() {
        // given
        PayrollConfirm confirm = PayrollConfirmFixture.saved(1L, PAY_MONTH);

        given(payrollConfirmRepository.findById(confirm.getId())).willReturn(Optional.of(confirm));
        given(payrollRepository.findByPayMonthAndStatus(confirm.getPayMonth(), PayrollStatus.CALCULATED))
                .willReturn(List.of(payroll));

        // when
        payrollConfirmService.confirm(confirm.getId(), confirmer);

        // then
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CONFIRMED);
        verify(autoVoucherService).createFromPayrollConfirm(confirm);
    }

    @Test
    @DisplayName("자동분개 생성 중 예외 발생 시 급여 확정 롤백")
    void confirmPayroll_rollback_when_auto_voucher_fails() {
        // given
        PayrollConfirm confirm = PayrollConfirmFixture.saved(1L, PAY_MONTH);

        given(payrollConfirmRepository.findById(confirm.getId())).willReturn(Optional.of(confirm));
        given(payrollRepository.findByPayMonthAndStatus(PAY_MONTH, PayrollStatus.CALCULATED)).willReturn(List.of(payroll));

        // 자동분개에서 예외 발생
        doThrow(new RuntimeException("AUTO VOUCHER FAIL")).when(autoVoucherService).createFromPayrollConfirm(any());

        // when
        assertThatThrownBy(() -> payrollConfirmService.confirm(confirm.getId(), confirmer))
                .isInstanceOf(RuntimeException.class);

        // then
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CREATED);
        verify(autoVoucherService).createFromPayrollConfirm(any());
    }

    @Test
    @DisplayName("회계기간 마감 시 급여 확정 불가")
    void confirmPayroll_fail_when_period_closed() {
        // given
        PayrollConfirm confirm = PayrollConfirmFixture.saved(1L, PAY_MONTH);

        given(payrollConfirmRepository.findById(confirm.getId())).willReturn(Optional.of(confirm));

        doThrow(new BusinessException(ErrorCode.INVALID_STATE, "회계기간 마감"))
                .when(accountingPeriodService).assertPeriodOpen(PAY_MONTH);

        // when & then
        assertThatThrownBy(() -> payrollConfirmService.confirm(confirm.getId(), confirmer))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
                    assertThat(be.getDetailMessage()).contains("회계기간 마감");
                });
    }

    @Test
    @DisplayName("급여 확정 취소 성공")
    void cancel_success() {
        // given
        PayrollConfirm confirm = PayrollConfirmFixture.confirmed(1L, PAY_MONTH, confirmer);

        given(payrollConfirmRepository.findById(confirm.getId())).willReturn(Optional.of(confirm));

        // when
        payrollConfirmService.cancel(confirm.getId(), confirmer);

        // then
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CANCELED);
        verify(autoVoucherService).cancelAutoVouchersBySource(SourceType.PAYROLL, confirm.getId(), confirmer);
    }
}
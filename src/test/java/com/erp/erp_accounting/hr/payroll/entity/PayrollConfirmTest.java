package com.erp.erp_accounting.hr.payroll.entity;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollConfirmFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("급여 확정 엔티티 테스트")
class PayrollConfirmTest {

    private PayrollConfirm confirm;
    private Payroll payroll;
    private User confirmer;

    @BeforeEach
    void setUp() {
        confirmer = UserFixture.accountingUser();
        ReflectionTestUtils.setField(confirmer, "id", 10L);

        Employee employee = EmployeeFixture.employee();
        payroll = PayrollFixture.calculatedPayroll(employee);

        confirm = PayrollConfirmFixture.created(YearMonth.of(2026, 1));
    }

    @Test
    @DisplayName("CALCULATED 급여를 급여 확정에 추가 성공")
    void addPayroll_success() {
        // when
        confirm.addPayroll(payroll);

        // then
        assertThat(confirm.getPayrolls()).contains(payroll);
        assertThat(payroll.getPayrollConfirm()).isEqualTo(confirm);
    }

    @Test
    @DisplayName("급여 월이 다르면 예외")
    void addPayroll_fail_when_payMonth_mismatch() {
        // given
        Payroll otherMonthPayroll = PayrollFixture.calculatedPayroll(EmployeeFixture.employee());
        ReflectionTestUtils.setField(otherMonthPayroll, "payMonth", YearMonth.of(2026, 2));

        // when & then
        assertThatThrownBy(() -> confirm.addPayroll(otherMonthPayroll))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
                });
    }

    @Test
    @DisplayName("CALCULATED 상태가 아니면 예외")
    void addPayroll_fail_when_status_invalid() {
        // given
        payroll.markConfirmed();

        // when & then
        assertThatThrownBy(() -> confirm.addPayroll(payroll))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
                });
    }

    @Test
    @DisplayName("급여 확정 처리 성공")
    void confirm_success() {
        // given
        confirm.addPayroll(payroll);

        // when
        confirm.confirm(confirmer);

        // then
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CONFIRMED);
        assertThat(confirm.getConfirmedBy()).isEqualTo(confirmer);
        assertThat(confirm.getConfirmedAt()).isNotNull();
        assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.CONFIRMED);
    }

    @Test
    @DisplayName("급여 확정 취소 성공")
    void cancel_success() {
        // given
        confirm.addPayroll(payroll);
        confirm.confirm(confirmer);

        // when
        confirm.cancel(confirmer);

        // then
        assertThat(confirm.getStatus()).isEqualTo(PayrollConfirmStatus.CANCELED);
        assertThat(confirm.getCanceledBy()).isEqualTo(confirmer);
        assertThat(confirm.getCanceledAt()).isNotNull();
        assertThat(confirm.getPayrolls()).isEmpty();
        assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.CALCULATED);
    }
}
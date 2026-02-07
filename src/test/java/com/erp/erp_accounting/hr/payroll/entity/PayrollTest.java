package com.erp.erp_accounting.hr.payroll.entity;

import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("급여 엔티티 테스트")
class PayrollTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = EmployeeFixture.employee();
    }

    @Test
    @DisplayName("급여 금액 계산 성공")
    void calculateNetAmount_success() {
        // given
        Payroll payroll = PayrollFixture.createdPayroll(employee);

        // when
        payroll.calculateNetAmount();

        // then
        assertThat(payroll.getNetAmount()).isEqualByComparingTo("3050000");
        assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.CALCULATED);
    }
}

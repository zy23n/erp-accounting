package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class EmployeeFixture {

    public static Employee employee() {
        return Employee.builder()
                .empNo("EMP-001")
                .name("김개발")
                .hireDate(LocalDate.of(2024, 1, 1))
                .user(UserFixture.normalUser())
                .position("사원")
                .department("개발팀")
                .defaultPaymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();
    }

    public static Employee employeeWithId(Long id) {
        Employee employee = employee();
        ReflectionTestUtils.setField(employee, "id", id);
        return employee;
    }
}

package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.YearMonth;

public class PayrollFixture {

    public static Payroll createdPayroll(Employee employee) {
        return Payroll.builder()
                .employee(employee)
                .payMonth(YearMonth.of(2026, 1))
                .baseSalary(BigDecimal.valueOf(3_000_000))
                .allowanceAmount(BigDecimal.valueOf(200_000))
                .deductionAmount(BigDecimal.valueOf(150_000))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();
    }

    public static Payroll calculatedPayroll(Employee employee) {
        Payroll payroll = createdPayroll(employee);
        payroll.calculateNetAmount();
        return payroll;
    }

    public static Payroll savedCreatedPayroll(Long id, Employee employee) {
        Payroll payroll = createdPayroll(employee);
        ReflectionTestUtils.setField(payroll, "id", id);
        return payroll;
    }

    public static Payroll savedCalculatedPayroll(Long id, Employee employee) {
        Payroll payroll = calculatedPayroll(employee);
        ReflectionTestUtils.setField(payroll, "id", id);
        return payroll;
    }
}
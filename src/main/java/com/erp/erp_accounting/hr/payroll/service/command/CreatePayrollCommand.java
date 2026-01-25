package com.erp.erp_accounting.hr.payroll.service.command;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class CreatePayrollCommand {
    private final Long employeeId;
    private final YearMonth payMonth;
    private final BigDecimal baseSalary;
    private final BigDecimal allowanceAmount;
    private final BigDecimal deductionAmount;
    private final PaymentMethod paymentMethod;
}

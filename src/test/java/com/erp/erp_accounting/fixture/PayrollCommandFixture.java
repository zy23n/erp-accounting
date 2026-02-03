package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.service.command.CreatePayrollCommand;

import java.math.BigDecimal;
import java.time.YearMonth;

public class PayrollCommandFixture {

    public static CreatePayrollCommand valid(Long employeeId) {
        return new CreatePayrollCommand(
                employeeId,
                YearMonth.of(2026, 1),
                BigDecimal.valueOf(3_000_000),
                BigDecimal.valueOf(200_000),
                BigDecimal.valueOf(150_000),
                PaymentMethod.BANK_TRANSFER
        );
    }
}

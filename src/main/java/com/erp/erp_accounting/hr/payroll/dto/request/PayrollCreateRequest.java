package com.erp.erp_accounting.hr.payroll.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollCreateRequest {
    private Long employeeId;
    private YearMonth payMonth;
    private BigDecimal baseSalary;
    private BigDecimal allowanceAmount;
    private BigDecimal deductionAmount;
}

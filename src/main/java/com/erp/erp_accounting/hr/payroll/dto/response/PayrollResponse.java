package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class PayrollResponse {
    private Long payrollId;
    private Long employeeId;
    private String employeeName;
    private YearMonth payMonth;

    private BigDecimal baseSalary;
    private BigDecimal allowanceAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netAmount;

    private PayrollStatus status;
}

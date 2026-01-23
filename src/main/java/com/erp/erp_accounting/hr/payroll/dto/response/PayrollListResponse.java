package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class PayrollListResponse {
    private Long payrollId;
    private Long employeeId;
    private String employeeName;
    private String empNo;
    private String department;
    private String position;

    private YearMonth payMonth;
    private BigDecimal netAmount;
    private PayrollStatus status;
    private PaymentMethod paymentMethod;
}

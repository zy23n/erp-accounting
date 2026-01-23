package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Builder
@AllArgsConstructor
public class PayrollResponse {
    private Long payrollId;
    private Long employeeId;
    private String employeeName;
    private String empNo;
    private YearMonth payMonth;

    private BigDecimal baseSalary;
    private BigDecimal allowanceAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netAmount;

    private PayrollStatus status;
    private PaymentMethod paymentMethod;

    public static PayrollResponse fromEntity(Payroll payroll) {
        return PayrollResponse.builder()
                .payrollId(payroll.getId())
                .employeeId(payroll.getEmployee().getId())
                .employeeName(payroll.getEmployee().getName())
                .empNo(payroll.getEmployee().getEmpNo())
                .payMonth(payroll.getPayMonth())
                .baseSalary(payroll.getBaseSalary())
                .allowanceAmount(payroll.getAllowanceAmount())
                .deductionAmount(payroll.getDeductionAmount())
                .netAmount(payroll.getNetAmount())
                .status(payroll.getStatus())
                .paymentMethod(payroll.getPaymentMethod())
                .build();
    }
}

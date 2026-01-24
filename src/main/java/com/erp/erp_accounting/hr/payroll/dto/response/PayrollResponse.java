package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "급여 상세 응답 DTO")
public class PayrollResponse {

    @Schema(description = "급여 ID", example = "100")
    private Long payrollId;

    @Schema(description = "직원 ID", example = "10")
    private Long employeeId;

    @Schema(description = "직원명", example = "홍길동")
    private String employeeName;

    @Schema(description = "사번", example = "EMP001")
    private String empNo;

    @Schema(description = "급여 월", example = "2026-01")
    private YearMonth payMonth;

    @Schema(description = "기본급", example = "3000000")
    private BigDecimal baseSalary;

    @Schema(description = "수당 합계", example = "200000")
    private BigDecimal allowanceAmount;

    @Schema(description = "공제 합계", example = "150000")
    private BigDecimal deductionAmount;

    @Schema(description = "실지급액", example = "3050000")
    private BigDecimal netAmount;

    @Schema(description = "급여 상태", example = "CONFIRMED")
    private PayrollStatus status;

    @Schema(description = "지급 수단", example = "BANK_TRANSFER")
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

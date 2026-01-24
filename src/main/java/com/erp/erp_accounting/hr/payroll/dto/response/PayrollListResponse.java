package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
@Schema(description = "급여 목록 응답 DTO")
public class PayrollListResponse {

    @Schema(description = "급여 ID", example = "100")
    private Long id;

    @Schema(description = "직원 ID", example = "10")
    private Long employeeId;

    @Schema(description = "직원명", example = "홍길동")
    private String employeeName;

    @Schema(description = "사번", example = "EMP001")
    private String empNo;

    @Schema(description = "부서명", example = "개발팀")
    private String department;

    @Schema(description = "직급", example = "대리")
    private String position;

    @Schema(description = "급여 월", example = "2026-01")
    private YearMonth payMonth;

    @Schema(description = "실지급액", example = "3050000")
    private BigDecimal netAmount;

    @Schema(description = "급여 상태", example = "CONFIRMED")
    private PayrollStatus status;

    @Schema(description = "지급 수단", example = "BANK_TRANSFER")
    private PaymentMethod paymentMethod;
}

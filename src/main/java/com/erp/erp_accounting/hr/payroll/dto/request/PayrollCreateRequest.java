package com.erp.erp_accounting.hr.payroll.dto.request;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "급여 생성 요청 DTO")
public class PayrollCreateRequest {

    @Schema(description = "직원 ID", required = true, example = "10")
    @NotNull(message = "직원 미입력")
    private Long employeeId;

    @Schema(description = "급여 월 (yyyy-MM)", required = true, example = "2026-01")
    @NotNull(message = "급여 월 미입력")
    private String payMonth;

    @Schema(description = "기본급", required = true, example = "3000000")
    @NotNull @Positive(message = "기본급 0이하")
    private BigDecimal baseSalary;

    @Schema(description = "수당 금액", required = true, example = "200000")
    @NotNull @PositiveOrZero(message = "수당 음수")
    private BigDecimal allowanceAmount;

    @Schema(description = "공제 금액", required = true, example = "150000")
    @NotNull @PositiveOrZero(message = "공제액 음수")
    private BigDecimal deductionAmount;

    @Schema(description = "지급 수단", required = true, example = "BANK_TRANSFER")
    private PaymentMethod paymentMethod;
}

package com.erp.erp_accounting.hr.payroll.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollCreateRequest {

    @NotNull(message = "직원을 선택해주세요.")
    private Long employeeId;

    @NotNull(message = "급여 월을 선택해주세요.")
    private YearMonth payMonth;

    @NotNull @Positive(message = "기본급은 0보다 커야 합니다.")
    private BigDecimal baseSalary;

    @NotNull @PositiveOrZero(message = "수당은 0 이상이어야 합니다.")
    private BigDecimal allowanceAmount;

    @NotNull @PositiveOrZero(message = "공제액은 0 이상이어야 합니다.")
    private BigDecimal deductionAmount;
}

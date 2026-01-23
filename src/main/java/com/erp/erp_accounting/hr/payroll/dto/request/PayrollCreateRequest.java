package com.erp.erp_accounting.hr.payroll.dto.request;

import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
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

    @NotNull(message = "직원 미입력")
    private Long employeeId;

    @NotNull(message = "급여 월 미입력")
    private YearMonth payMonth;

    @NotNull @Positive(message = "기본급 0이하")
    private BigDecimal baseSalary;

    @NotNull @PositiveOrZero(message = "수당 음수")
    private BigDecimal allowanceAmount;

    @NotNull @PositiveOrZero(message = "공제액 음수")
    private BigDecimal deductionAmount;

    @NotNull(message = "지급수단 미입력")
    private PaymentMethod paymentMethod;
}

package com.erp.erp_accounting.accounting.ledger.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계정 원장 조회 조건 DTO")
public class AccountLedgerSearchCondition {

    @Schema(description = "계정 ID", required = true, example = "1000")
    @NotNull(message = "계정과목 미입력")
    private Long accountId;

    @Schema(description = "조회 시작일", example = "2026-01-01")
    @PastOrPresent(message = "조회 시작일 범위 오류")
    private LocalDate startDate;

    @Schema(description = "조회 종료일", example = "2026-01-31")
    private LocalDate endDate;

    @JsonIgnore
    public LocalDate getStartDateOrDefault() {
        if (startDate != null) return startDate;
        if (endDate != null) return endDate.withDayOfMonth(1);
        return YearMonth.now().atDay(1);
    }

    @JsonIgnore
    public LocalDate getEndDateOrDefault() {
        if (endDate != null) return endDate;
        if (startDate != null) return YearMonth.from(startDate).atEndOfMonth();
        return YearMonth.now().atEndOfMonth();
    }
}

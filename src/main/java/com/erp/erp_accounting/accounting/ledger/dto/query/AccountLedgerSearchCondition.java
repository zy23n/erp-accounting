package com.erp.erp_accounting.accounting.ledger.dto.query;

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
public class AccountLedgerSearchCondition {

    @NotNull(message = "계정과목 미입력")
    private Long accountId;

    @PastOrPresent(message = "조회 시작일 범위 오류")
    private LocalDate startDate;

    private LocalDate endDate;

    public LocalDate getStartDateOrDefault() {
        if (startDate != null) return startDate;
        if (endDate != null) return endDate.withDayOfMonth(1);
        return YearMonth.now().atDay(1);
    }

    public LocalDate getEndDateOrDefault() {
        if (endDate != null) return endDate;
        if (startDate != null) return YearMonth.from(startDate).atEndOfMonth();
        return YearMonth.now().atEndOfMonth();
    }
}

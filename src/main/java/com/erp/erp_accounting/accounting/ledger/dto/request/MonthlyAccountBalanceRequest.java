package com.erp.erp_accounting.accounting.ledger.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAccountBalanceRequest {

    @NotNull(message = "계정 ID는 필수입니다.")
    private Long accountId;

    @NotNull(message = "조회 월은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth month;

    @AssertTrue(message = "조회 월은 현재 월 이후일 수 없습니다.")
    public boolean isNotFutureMonth() {
        if (month == null) return true;
        return !month.isAfter(YearMonth.now());
    }
}
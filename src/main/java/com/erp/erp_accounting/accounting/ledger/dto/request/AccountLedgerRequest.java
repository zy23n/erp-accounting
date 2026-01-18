package com.erp.erp_accounting.accounting.ledger.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountLedgerRequest {

    @NotNull(message = "계정과목 미입력")
    private Long accountId;

    @NotNull(message = "조회 시작일 미입력")
    @PastOrPresent(message = "조회 시작일 범위 오류")
    private LocalDate startDate;

    @NotNull(message = "조회 종료일 미입력")
    private LocalDate endDate;

    @AssertTrue(message = "조회 기간 범위 오류")
    public boolean isValidPeriod() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}

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

    @NotNull(message = "계정 ID는 필수입니다.")
    private Long accountId;

    @NotNull(message = "조회 시작일은 필수입니다.")
    @PastOrPresent(message = "조회 시작일은 과거 또는 오늘이어야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "조회 종료일은 필수입니다.")
    private LocalDate endDate;

    @AssertTrue(message = "조회 종료일은 조회 시작일 이후여야 합니다.")
    public boolean isValidPeriod() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}

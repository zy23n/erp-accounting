package com.erp.erp_accounting.accounting.ledger.dto.response;

import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Schema(description = "월별 계정 잔액 응답 DTO")
public class MonthlyAccountBalanceResponse {

    @Schema(description = "월 초 잔액", example = "50000")
    private BigDecimal openingBalance;

    @Schema(description = "총 차변", example = "100000")
    private BigDecimal totalDebit;

    @Schema(description = "총 대변", example = "50000")
    private BigDecimal totalCredit;

    @Schema(description = "월 말 잔액", example = "100000")
    private BigDecimal closingBalance;

    public static MonthlyAccountBalanceResponse of(BigDecimal opening, BigDecimal debit, BigDecimal credit, BigDecimal closing) {
        return new MonthlyAccountBalanceResponse(
                opening != null ? opening : BigDecimal.ZERO,
                debit != null ? debit : BigDecimal.ZERO,
                credit != null ? credit : BigDecimal.ZERO,
                closing != null ? closing : BigDecimal.ZERO
        );
    }

    public static MonthlyAccountBalanceResponse fromSnapshot(MonthlyAccountBalance balance) {
        return of(balance.getOpeningBalance(), balance.getDebitSum(), balance.getCreditSum(), balance.getClosingBalance());
    }

    public static MonthlyAccountBalanceResponse fromRealtime(BigDecimal opening, BigDecimal debit, BigDecimal credit, BigDecimal closing) {
        return of(opening, debit, credit, closing);
    }
}

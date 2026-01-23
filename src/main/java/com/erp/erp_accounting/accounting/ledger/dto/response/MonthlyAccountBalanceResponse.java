package com.erp.erp_accounting.accounting.ledger.dto.response;

import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MonthlyAccountBalanceResponse {
    private BigDecimal openingBalance;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
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

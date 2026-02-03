package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LedgerItemFixture {

    public static AccountLedgerItemDto deposit(LocalDate date, String voucherNo, String description, String amount) {
        return new AccountLedgerItemDto(
                date,
                voucherNo,
                description,
                new BigDecimal(amount), // debit
                BigDecimal.ZERO,         // credit
                null                     // balance
        );
    }

    public static AccountLedgerItemDto withdraw(LocalDate date, String voucherNo, String description, String amount) {
        return new AccountLedgerItemDto(
                date,
                voucherNo,
                description,
                BigDecimal.ZERO,         // debit
                new BigDecimal(amount),  // credit
                null
        );
    }
}
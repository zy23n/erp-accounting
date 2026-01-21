package com.erp.erp_accounting.accounting.ledger.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AccountLedgerItemDto {
    private LocalDate voucherDate;
    private String voucherNo;
    private String description;

    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    private BigDecimal balance;
}

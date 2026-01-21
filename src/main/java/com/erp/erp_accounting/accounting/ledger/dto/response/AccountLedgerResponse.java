package com.erp.erp_accounting.accounting.ledger.dto.response;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class AccountLedgerResponse {
    BigDecimal openingBalance;
    BigDecimal closingBalance;
    List<AccountLedgerItemDto> items;
}

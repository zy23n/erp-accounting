package com.erp.erp_accounting.accounting.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OpeningBalanceDto {
    private BigDecimal debitSum;
    private BigDecimal creditSum;
}

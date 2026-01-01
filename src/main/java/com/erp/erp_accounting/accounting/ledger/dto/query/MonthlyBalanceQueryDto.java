package com.erp.erp_accounting.accounting.ledger.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MonthlyBalanceQueryDto {
    private BigDecimal debitSum;
    private BigDecimal creditSum;
}

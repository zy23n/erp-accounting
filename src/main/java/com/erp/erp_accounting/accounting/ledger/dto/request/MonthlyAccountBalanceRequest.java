package com.erp.erp_accounting.accounting.ledger.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAccountBalanceRequest {
    private Long accountId;
    private YearMonth month;
}
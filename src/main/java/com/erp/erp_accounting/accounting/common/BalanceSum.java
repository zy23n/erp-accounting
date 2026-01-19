package com.erp.erp_accounting.accounting.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BalanceSum {
    private final BigDecimal debit;
    private final BigDecimal credit;

    public boolean isBalanced() {
        return debit.compareTo(credit) == 0;
    }
}
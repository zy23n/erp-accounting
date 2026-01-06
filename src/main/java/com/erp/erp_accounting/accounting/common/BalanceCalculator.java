package com.erp.erp_accounting.accounting.common;

import com.erp.erp_accounting.accounting.account.entity.NormalBalance;

import java.math.BigDecimal;

public class BalanceCalculator {

    // 정상 잔액 방향에 따른 잔액 계산
    public static BigDecimal applyNormalBalance(NormalBalance normalBalance, BigDecimal base,
                                                BigDecimal debit, BigDecimal credit) {
        if (base == null) base = BigDecimal.ZERO;
        if (debit == null) debit = BigDecimal.ZERO;
        if (credit == null) credit = BigDecimal.ZERO;

        if (normalBalance == NormalBalance.DEBIT) {
            return base.add(debit).subtract(credit);
        } else {
            return base.add(credit).subtract(debit);
        }
    }
}

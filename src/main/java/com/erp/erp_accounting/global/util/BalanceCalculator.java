package com.erp.erp_accounting.global.util;

import com.erp.erp_accounting.accounting.account.entity.NormalBalance;

import java.math.BigDecimal;

public class BalanceCalculator {
    /**
     * 정상 잔액 방향에 따른 잔액 계산
     *
     * @param normalBalance 계정의 정상잔액 방향
     * @param base 이전 잔액
     * @param debit 차변 금액
     * @param credit 대변 금액
     * @return 계산된 잔액
     */
    public static BigDecimal applyNormalBalance(NormalBalance normalBalance, BigDecimal base,
                                                BigDecimal debit, BigDecimal credit) {
        if (normalBalance == NormalBalance.DEBIT) {
            return base.add(debit).subtract(credit);
        } else {
            return base.add(credit).subtract(debit);
        }
    }
}

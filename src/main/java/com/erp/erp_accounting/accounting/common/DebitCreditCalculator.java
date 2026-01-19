package com.erp.erp_accounting.accounting.common;

import com.erp.erp_accounting.accounting.voucher.entity.LineAmount;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;

import java.math.BigDecimal;
import java.util.List;

public final class DebitCreditCalculator {

    public static BalanceSum calculate(List<? extends LineAmount> lines) {
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;

        for (LineAmount line : lines) {
            if (line.getType() == LineType.DEBIT) {
                debit = debit.add(line.getAmount());
            } else if (line.getType() == LineType.CREDIT) {
                credit = credit.add(line.getAmount());
            }
        }
        return new BalanceSum(debit, credit);
    }
}

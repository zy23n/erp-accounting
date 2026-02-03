package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto;

public class MonthlyBalanceQueryFixture {

    public static MonthlyBalanceQueryDto of(String debit, String credit) {
        return new MonthlyBalanceQueryDto(
                Money.of(debit),
                Money.of(credit)
        );
    }

    public static MonthlyBalanceQueryDto zero() {
        return new MonthlyBalanceQueryDto(
                Money.zero(),
                Money.zero()
        );
    }
}
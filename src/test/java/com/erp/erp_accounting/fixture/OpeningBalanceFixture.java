package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;

import java.math.BigDecimal;

public class OpeningBalanceFixture {

    public static OpeningBalanceDto of(String debit, String credit) {
        return new OpeningBalanceDto(
                new BigDecimal(debit),
                new BigDecimal(credit)
        );
    }
}
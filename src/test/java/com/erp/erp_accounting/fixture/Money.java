package com.erp.erp_accounting.fixture;

import java.math.BigDecimal;

public class Money {

    public static BigDecimal of(String value) {
        return new BigDecimal(value);
    }

    public static BigDecimal zero() {
        return BigDecimal.ZERO;
    }
}

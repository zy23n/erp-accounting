package com.erp.erp_accounting.accounting.voucher.entity;

import java.math.BigDecimal;

public interface LineAmount {
    LineType getType();
    BigDecimal getAmount();
}
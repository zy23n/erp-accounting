package com.erp.erp_accounting.accounting.account.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountCategory {
    ASSET("자산"),
    LIABILITY("부채"),
    EQUITY("자본"),
    REVENUE("수익"),
    EXPENSE("비용");

    private final String koreanName;
}

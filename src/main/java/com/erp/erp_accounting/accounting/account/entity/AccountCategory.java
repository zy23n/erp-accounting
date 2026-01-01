package com.erp.erp_accounting.accounting.account.entity;

import lombok.Getter;

@Getter
public enum AccountCategory {
    ASSET("자산"),
    LIABILITY("부채"),
    EQUITY("자본"),
    REVENUE("수익"),
    EXPENSE("비용");

    private final String koreanName;

    AccountCategory(String koreanName) {
        this.koreanName = koreanName;
    }

}

package com.erp.erp_accounting.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ACCOUNTING("ROLE_ACCOUNTING"),
    HR("ROLE_HR");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}

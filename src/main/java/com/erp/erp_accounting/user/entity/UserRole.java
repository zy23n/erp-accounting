package com.erp.erp_accounting.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ACCOUNTING("ROLE_ACCOUNTING"),
    HR("ROLE_HR");

    private final String authority;
}

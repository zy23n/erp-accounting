package com.erp.erp_accounting.hr.payroll.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CASH("1010"),
    BANK_TRANSFER("1020");

    private final String accountCode;
}

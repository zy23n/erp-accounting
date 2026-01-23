package com.erp.erp_accounting.hr.payroll.entity;

public enum PaymentMethod {
    CASH("1010"),
    BANK_TRANSFER("1020");

    private final String accountCode;

    PaymentMethod(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountCode() {
        return accountCode;
    }
}

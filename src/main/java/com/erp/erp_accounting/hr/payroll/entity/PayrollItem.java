package com.erp.erp_accounting.hr.payroll.entity;

public enum PayrollItem {
    BASE_SALARY("5010"),
    BONUS("5020"),
    DEDUCTION("2020"),
    CASH("1010");

    private final String accountCode;

    PayrollItem(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountCode() {
        return accountCode;
    }
}

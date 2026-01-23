package com.erp.erp_accounting.hr.payroll.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayrollItem {
    BASE_SALARY("5010"),
    BONUS("5020"),
    DEDUCTION("2020");

    private final String accountCode;
}

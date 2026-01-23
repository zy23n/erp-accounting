package com.erp.erp_accounting.accounting.voucher.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceType {
    NONE(VoucherType.GENERAL),
    PAYROLL(VoucherType.PAYROLL);

    private final VoucherType voucherType;
}

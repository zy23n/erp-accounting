package com.erp.erp_accounting.accounting.voucher.entity;

public enum SourceType {
    NONE(VoucherType.GENERAL),
    PAYROLL(VoucherType.PAYROLL);

    private final VoucherType voucherType;

    SourceType(VoucherType voucherType) {
        this.voucherType = voucherType;
    }

    public VoucherType getVoucherType() {
        return voucherType;
    }
}

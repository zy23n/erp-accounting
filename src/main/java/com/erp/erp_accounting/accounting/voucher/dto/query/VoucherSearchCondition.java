package com.erp.erp_accounting.accounting.voucher.dto.query;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherSearchCondition {
    private String voucherNo;
    private VoucherStatus status;
    private VoucherType voucherType;
    private SourceType sourceType;
    private LocalDate voucherDate;
    private LocalDate startDate;
    private LocalDate endDate;
}

package com.erp.erp_accounting.accounting.voucher.dto.request;

import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherLineRequest {
    private Long accountId;
    private LineType type;
    private BigDecimal amount;
}

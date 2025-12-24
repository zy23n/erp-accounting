package com.erp.erp_accounting.accounting.voucher.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VoucherLineResponse {
    private Long id;
    private Long accountId;
    private String accountName;
    private String type; // DEBIT / CREDIT
    private BigDecimal amount;
}
package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.VoucherLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class VoucherLineResponse {
    private Long id;
    private Long accountId;
    private String accountName;
    private String type; // DEBIT / CREDIT
    private BigDecimal amount;

    public static VoucherLineResponse fromEntity(VoucherLine line) {
        return VoucherLineResponse.builder()
                .id(line.getId())
                .accountId(line.getAccount().getId())
                .accountName(line.getAccount().getName())
                .type(line.getType().name())
                .amount(line.getAmount())
                .build();
    }

}
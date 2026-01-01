package com.erp.erp_accounting.accounting.ledger.dto;

import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// Repository 전용 조회 DTO
@Getter
@AllArgsConstructor
public class AccountLedgerQueryDto {
    private LocalDate voucherDate;
    private String voucherNo;
    private String description;

    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public AccountLedgerQueryDto(
            LocalDate voucherDate,
            String voucherNo,
            String description,
            LineType type,
            BigDecimal amount
    ) {
        this.voucherDate = voucherDate;
        this.voucherNo = voucherNo;
        this.description = description;

        if (type == LineType.DEBIT) {
            this.debitAmount = amount;
            this.creditAmount = BigDecimal.ZERO;
        } else {
            this.debitAmount = BigDecimal.ZERO;
            this.creditAmount = amount;
        }
    }
}
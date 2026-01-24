package com.erp.erp_accounting.accounting.ledger.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "계정 원장 항목 DTO")
public class AccountLedgerItemDto {

    @Schema(description = "전표 일자", example = "2026-01-15")
    private LocalDate voucherDate;

    @Schema(description = "전표 번호", example = "V1769281057499")
    private String voucherNo;

    @Schema(description = "적요", example = "급여 지급")
    private String description;

    @Schema(description = "차변 금액", example = "100000")
    private BigDecimal debitAmount;

    @Schema(description = "대변 금액", example = "0")
    private BigDecimal creditAmount;

    @Schema(description = "잔액", example = "50000")
    private BigDecimal balance;
}

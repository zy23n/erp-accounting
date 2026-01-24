package com.erp.erp_accounting.accounting.ledger.dto.response;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "계정 원장 응답 DTO")
public class AccountLedgerResponse {

    @Schema(description = "기초 잔액", example = "100000.00")
    BigDecimal openingBalance;

    @Schema(description = "기말 잔액", example = "120000.00")
    BigDecimal closingBalance;

    @Schema(description = "계정 원장 항목 리스트")
    List<AccountLedgerItemDto> items;
}

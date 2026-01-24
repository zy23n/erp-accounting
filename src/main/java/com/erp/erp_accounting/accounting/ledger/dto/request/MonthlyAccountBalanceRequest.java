package com.erp.erp_accounting.accounting.ledger.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 계정 잔액 조회 조건 DTO")
public class MonthlyAccountBalanceRequest {

    @Schema(description = "계정 ID", required = true, example = "1")
    @NotNull(message = "계정 미입력")
    private Long accountId;

    @Schema(description = "조회 월 (yyyy-MM)", required = true, example = "2026-01")
    @NotBlank(message = "조회 월 미입력")
    private String month;
}
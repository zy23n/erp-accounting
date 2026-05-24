package com.erp.erp_accounting.accounting.ledger.dto.response;

import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.ledger.entity.BalanceSource;
import com.erp.erp_accounting.accounting.ledger.service.command.MonthlyAccountBalanceCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
@Schema(description = "월별 계정 잔액 응답 DTO")
public class MonthlyAccountBalanceResponse {

    @Schema(description = "계정 ID", example = "9")
    private Long accountId;

    @Schema(description = "조회 월", example = "2026-01")
    private YearMonth month;

    @Schema(description = "잔액 조회 방식", example = "SNAPSHOT")
    private BalanceSource source;

    @Schema(description = "월 초 잔액", example = "50000")
    private BigDecimal openingBalance;

    @Schema(description = "총 차변", example = "100000")
    private BigDecimal totalDebit;

    @Schema(description = "총 대변", example = "50000")
    private BigDecimal totalCredit;

    @Schema(description = "월 말 잔액", example = "100000")
    private BigDecimal closingBalance;

    public static MonthlyAccountBalanceResponse of(
            Long accountId,
            YearMonth month,
            BalanceSource source,
            BigDecimal opening,
            BigDecimal debit,
            BigDecimal credit,
            BigDecimal closing
    ) {
        return new MonthlyAccountBalanceResponse(
                accountId,
                month,
                source,
                opening != null ? opening : BigDecimal.ZERO,
                debit != null ? debit : BigDecimal.ZERO,
                credit != null ? credit : BigDecimal.ZERO,
                closing != null ? closing : BigDecimal.ZERO
        );
    }

    public static MonthlyAccountBalanceResponse fromSnapshot(MonthlyAccountBalance balance) {
        return of(
                balance.getAccount().getId(),
                balance.getPeriod(),
                BalanceSource.SNAPSHOT,
                balance.getOpeningBalance(),
                balance.getDebitSum(),
                balance.getCreditSum(),
                balance.getClosingBalance()
        );
    }

    public static MonthlyAccountBalanceResponse fromRealtime(
            MonthlyAccountBalanceCommand command,
            BigDecimal opening,
            BigDecimal debit,
            BigDecimal credit,
            BigDecimal closing
    ) {
        return of(
                command.getAccountId(),
                command.getMonth(),
                BalanceSource.REALTIME,
                opening,
                debit,
                credit,
                closing
        );
    }
}

package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.service.AccountLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "계정 원장", description = "계정 원장 조회 API")
public class AccountLedgerController {

    private final AccountLedgerService accountLedgerService;

    @Operation(summary = "계정별 원장 조회", description = "주어진 기간과 계정 ID 기준으로 계정 원장 내역을 조회합니다.")
    @GetMapping("/account")
    public ResponseEntity<AccountLedgerResponse> getAccountLedger(
            @Parameter(description = "조회 조건") @Valid @ModelAttribute AccountLedgerSearchCondition condition
    ) {
        log.info("[ACCOUNT_LEDGER] action=QUERY_REQUEST, accountId={}, startDate={}, endDate={}",
                condition.getAccountId(), condition.getStartDateOrDefault(), condition.getEndDateOrDefault());

        AccountLedgerResponse response = accountLedgerService.searchAccountLedger(condition);

        log.info("[ACCOUNT_LEDGER] action=QUERY_COMPLETE, accountId={}, startDate={}, endDate={}, openingBalance={}, closingBalance={}, rows={}",
                condition.getAccountId(), condition.getStartDateOrDefault(), condition.getEndDateOrDefault(),
                response.getOpeningBalance(), response.getClosingBalance(), response.getItems().size());

        return ResponseEntity.ok(response);
    }
}

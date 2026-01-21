package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.service.AccountLedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class AccountLedgerController {

    private final AccountLedgerService accountLedgerService;

    @GetMapping("/account")
    public ResponseEntity<AccountLedgerResponse> getAccountLedger(
            @Valid @ModelAttribute AccountLedgerSearchCondition condition
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

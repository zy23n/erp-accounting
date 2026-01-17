package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.request.AccountLedgerRequest;
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
            @Valid @ModelAttribute AccountLedgerRequest request
    ) {
        log.info("AccountLedger 조회 요청: accountId={}, startDate={}, endDate={}",
                request.getAccountId(), request.getStartDate(), request.getEndDate());

        AccountLedgerResponse response = accountLedgerService.getAccountLedger(request);

        log.info("AccountLedger 조회 완료: openingBalance={}, closingBalance={}, rows={}",
                response.getOpeningBalance(), response.getClosingBalance(), response.getItems().size());

        return ResponseEntity.ok(response);
    }
}

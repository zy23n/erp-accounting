package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.request.MonthlyAccountBalanceRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.service.MonthlyAccountBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class MonthlyAccountBalanceController {

    private final MonthlyAccountBalanceService monthlyAccountBalanceService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAccountBalanceResponse> getMonthlyAccountBalance(
            @Valid @ModelAttribute MonthlyAccountBalanceRequest request
    ) {
        log.info("[MONTHLY_BALANCE] action=QUERY_REQUEST, accountId={}, month={}", request.getAccountId(), request.getMonth());

        MonthlyAccountBalanceResponse response = monthlyAccountBalanceService.getMonthlyBalance(request);

        log.info("[MONTHLY_BALANCE] action=QUERY_COMPLETE, accountId={}, month={}, openingBalance={}, totalDebit={}, totalCredit={}, closingBalance={}",
                request.getAccountId(), request.getMonth(), response.getOpeningBalance(),
                response.getTotalDebit(), response.getTotalCredit(), response.getClosingBalance());

        return ResponseEntity.ok(response);
    }
}

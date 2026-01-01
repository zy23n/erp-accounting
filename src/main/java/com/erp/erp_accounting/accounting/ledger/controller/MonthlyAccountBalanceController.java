package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.request.MonthlyAccountBalanceRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.service.MonthlyAccountBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class MonthlyAccountBalanceController {

    private final MonthlyAccountBalanceService monthlyAccountBalanceService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAccountBalanceResponse> getMonthlyAccountBalance(
            @RequestParam("accountId") Long accountId,
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        MonthlyAccountBalanceRequest request = new MonthlyAccountBalanceRequest(accountId, month);
        return ResponseEntity.ok(monthlyAccountBalanceService.getMonthlyBalance(request));
    }
}

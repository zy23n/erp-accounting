package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.request.MonthlyAccountBalanceRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.service.MonthlyAccountBalanceService;
import com.erp.erp_accounting.accounting.ledger.service.command.MonthlyAccountBalanceCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Slf4j
@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "월별 계정 잔액", description = "계정별 월별 잔액 조회 API")
public class MonthlyAccountBalanceController {

    private final MonthlyAccountBalanceService monthlyAccountBalanceService;

    @Operation(summary = "월별 계정 잔액 조회", description = "지정한 계정과 조회월에 대한 월별 잔액 정보를 조회합니다.")
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAccountBalanceResponse> getMonthlyAccountBalance(
            @Valid @ModelAttribute MonthlyAccountBalanceRequest request
    ) {
        log.info("[MONTHLY_BALANCE] action=QUERY_REQUEST, accountId={}, month={}", request.getAccountId(), request.getMonth());

        MonthlyAccountBalanceCommand command = new MonthlyAccountBalanceCommand(
                request.getAccountId(),
                YearMonth.parse(request.getMonth())
        );
        command.validate();

        MonthlyAccountBalanceResponse response = monthlyAccountBalanceService.getMonthlyBalance(command);

        log.info("[MONTHLY_BALANCE] action=QUERY_COMPLETE, accountId={}, month={}, openingBalance={}, totalDebit={}, totalCredit={}, closingBalance={}",
                request.getAccountId(), request.getMonth(), response.getOpeningBalance(),
                response.getTotalDebit(), response.getTotalCredit(), response.getClosingBalance());

        return ResponseEntity.ok(response);
    }
}

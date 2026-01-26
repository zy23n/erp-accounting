package com.erp.erp_accounting.accounting.ledger.controller;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.service.AccountLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(accountLedgerService.searchAccountLedger(condition));
    }
}

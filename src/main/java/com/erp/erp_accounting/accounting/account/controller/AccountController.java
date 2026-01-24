package com.erp.erp_accounting.accounting.account.controller;

import com.erp.erp_accounting.accounting.account.dto.response.AccountTreeResponse;
import com.erp.erp_accounting.accounting.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "계정 관련 API")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "전체 계정 트리 조회", description = "모든 계정과 하위 계정 트리를 조회합니다.")
    @GetMapping("/tree")
    public ResponseEntity<List<AccountTreeResponse>> getAccountTree() {
        return ResponseEntity.ok(accountService.getAccountTree());
    }

    @Operation(summary = "Leaf 계정 조회", description = "하위 계정이 없는 말단 계정(Leaf)만 조회합니다.")
    @GetMapping("/leaf")
    public ResponseEntity<List<AccountTreeResponse>> getLeafAccounts() {
        return ResponseEntity.ok(accountService.getLeafAccounts());
    }
}

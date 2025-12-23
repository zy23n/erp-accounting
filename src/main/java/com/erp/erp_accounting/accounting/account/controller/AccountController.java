package com.erp.erp_accounting.accounting.account.controller;

import com.erp.erp_accounting.accounting.account.dto.response.AccountTreeResponse;
import com.erp.erp_accounting.accounting.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 전체 계정 트리 조회
    @GetMapping("/tree")
    public ResponseEntity<List<AccountTreeResponse>> getAccountTree() {
        return ResponseEntity.ok(accountService.getAccountTree());
    }

    // leaf 계정만 조회
    @GetMapping("/leaf")
    public ResponseEntity<List<AccountTreeResponse>> getLeafAccounts() {
        return ResponseEntity.ok(accountService.getLeafAccounts());
    }
}

package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/payroll-confirms")
@RequiredArgsConstructor
public class PayrollConfirmController {

    private final PayrollConfirmService payrollConfirmService;

    // 급여 확정 생성
    @PostMapping
    public ResponseEntity<Long> createConfirm(@RequestParam("payMonth") String payMonth) {
        YearMonth ym = YearMonth.parse(payMonth); // "2025-12"
        return ResponseEntity.ok(payrollConfirmService.createConfirm(ym));
    }

    // 급여 확정 처리
    @PostMapping("/{payrollConfirmId}/confirm")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> confirm(
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        payrollConfirmService.confirm(payrollConfirmId, principal.getUser());
        return ResponseEntity.ok().build();
    }

    // 급여 확정 취소
    @PostMapping("/{payrollConfirmId}/cancel")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> cancel(
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        payrollConfirmService.cancel(payrollConfirmId, principal.getUser());
        return ResponseEntity.ok().build();
    }
}
package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> confirm(
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @RequestParam("userId") Long userId // 추후 로그인/권한 적용
    ) {
        payrollConfirmService.confirm(payrollConfirmId, userId);
        return ResponseEntity.ok().build();
    }

    // 급여 확정 취소
    @PostMapping("/{payrollConfirmId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @RequestParam("userId") Long userId // 추후 로그인/권한 적용
    ) {
        payrollConfirmService.cancel(payrollConfirmId, userId);
        return ResponseEntity.ok().build();
    }
}
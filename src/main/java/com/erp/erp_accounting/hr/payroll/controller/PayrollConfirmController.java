package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/payroll-confirms")
@RequiredArgsConstructor
@Tag(name = "급여 확정", description = "급여 월별 확정 생성 및 확정/취소 처리 API")
public class PayrollConfirmController {

    private final PayrollConfirmService payrollConfirmService;

    @Operation(summary = "급여 확정 생성", description = "지정한 급여 월(payMonth)에 대해 급여 확정 데이터를 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createConfirm(
            @Parameter(description = "급여 월 (yyyy-MM)", required = true, example = "2026-01")
            @RequestParam("payMonth") @NotBlank String payMonth
    ) {
        return ResponseEntity.ok(payrollConfirmService.createConfirm(YearMonth.parse(payMonth)));
    }

    @Operation(summary = "급여 확정 처리", description = "급여 확정 건을 확정 상태로 처리합니다.")
    @PostMapping("/{payrollConfirmId}/confirm")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> confirm(
            @Parameter(description = "급여 확정 ID", required = true, example = "1")
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        payrollConfirmService.confirm(payrollConfirmId, principal.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "급여 확정 취소", description = "급여 확정 상태를 취소 처리합니다.")
    @PostMapping("/{payrollConfirmId}/cancel")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "급여 확정 ID", required = true, example = "1")
            @PathVariable("payrollConfirmId") Long payrollConfirmId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        payrollConfirmService.cancel(payrollConfirmId, principal.getUser());
        return ResponseEntity.ok().build();
    }
}
package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.service.PayrollQueryService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollQueryController {

    private final PayrollQueryService payrollQueryService;

    // 상세 조회
    @GetMapping("/{payrollId}")
    public ResponseEntity<PayrollResponse> getPayroll(
            @PathVariable("payrollId") Long payrollId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(payrollQueryService.getPayroll(payrollId, principal));
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getPayrollList(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(payrollQueryService.getPayrollList(principal));
    }
}

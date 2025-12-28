package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.service.PayrollQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollQueryController {

    private final PayrollQueryService payrollQueryService;

    // 단건 조회
    @GetMapping("/{payrollId}")
    public ResponseEntity<PayrollResponse> getPayroll(@PathVariable("payrollId") Long payrollId) {
        return ResponseEntity.ok(payrollQueryService.getPayroll(payrollId));
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getPayrollList() {
        return ResponseEntity.ok(payrollQueryService.getPayrollList());
    }
}

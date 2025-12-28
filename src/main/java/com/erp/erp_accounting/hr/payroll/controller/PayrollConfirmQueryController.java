package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmListResponse;
import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-confirms")
@RequiredArgsConstructor
public class PayrollConfirmQueryController {

    private final PayrollConfirmQueryService payrollConfirmQueryService;

    // 단건 조회
    @GetMapping("/{payrollConfirmId}")
    public ResponseEntity<PayrollConfirmResponse> getPayrollConfirm(
            @PathVariable("payrollConfirmId") Long payrollConfirmId) {
        return ResponseEntity.ok(payrollConfirmQueryService.getPayrollConfirm(payrollConfirmId));
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<PayrollConfirmListResponse>> getPayrollConfirmList() {
        return ResponseEntity.ok(payrollConfirmQueryService.getPayrollConfirmList());
    }
}
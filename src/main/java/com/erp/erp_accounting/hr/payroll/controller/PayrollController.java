package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.request.PayrollCreateRequest;
import com.erp.erp_accounting.hr.payroll.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // 급여 생성(입력 + 계산)
    @PostMapping
    public ResponseEntity<Long> createPayroll(@RequestBody @Valid PayrollCreateRequest request) {
        return ResponseEntity.ok(payrollService.createPayroll(request));
    }
}

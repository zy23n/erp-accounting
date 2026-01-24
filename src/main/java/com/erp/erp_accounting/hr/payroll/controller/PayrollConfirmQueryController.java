package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmListResponse;
import com.erp.erp_accounting.hr.payroll.service.PayrollConfirmQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-confirms")
@RequiredArgsConstructor
@Tag(name = "급여 확정 조회", description = "급여 확정 단건 및 목록 조회 API")
public class PayrollConfirmQueryController {

    private final PayrollConfirmQueryService payrollConfirmQueryService;

    @Operation(summary = "급여 확정 단건 조회", description = "급여 확정 ID로 급여 확정 상세 정보를 조회합니다.")
    @GetMapping("/{payrollConfirmId}")
    public ResponseEntity<PayrollConfirmResponse> getPayrollConfirm(
            @Parameter(description = "급여 확정 ID", required = true, example = "1")
            @PathVariable("payrollConfirmId") Long payrollConfirmId
    ) {
        return ResponseEntity.ok(payrollConfirmQueryService.getPayrollConfirm(payrollConfirmId));
    }

    @Operation(summary = "급여 확정 목록 조회", description = "전체 급여 확정 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PayrollConfirmListResponse>> getPayrollConfirmList() {
        return ResponseEntity.ok(payrollConfirmQueryService.getPayrollConfirmList());
    }
}
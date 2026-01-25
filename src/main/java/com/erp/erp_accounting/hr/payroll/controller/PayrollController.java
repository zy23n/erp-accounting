package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.request.PayrollCreateRequest;
import com.erp.erp_accounting.hr.payroll.service.PayrollService;
import com.erp.erp_accounting.hr.payroll.service.command.CreatePayrollCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
@Tag(name = "급여", description = "급여 생성 및 관리 API")
public class PayrollController {

    private final PayrollService payrollService;

    @Operation(summary = "급여 생성", description = "직원, 급여 월, 급여 항목 정보를 입력받아 급여를 생성하고 계산합니다.")
    @PostMapping
    public ResponseEntity<Long> createPayroll(@Valid @RequestBody PayrollCreateRequest request) {

        CreatePayrollCommand command = new CreatePayrollCommand(
                request.getEmployeeId(),
                YearMonth.parse(request.getPayMonth()),
                request.getBaseSalary(),
                request.getAllowanceAmount(),
                request.getDeductionAmount(),
                request.getPaymentMethod()
        );

        return ResponseEntity.ok(payrollService.createPayroll(command));
    }
}

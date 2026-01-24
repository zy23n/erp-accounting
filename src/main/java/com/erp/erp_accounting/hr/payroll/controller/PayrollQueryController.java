package com.erp.erp_accounting.hr.payroll.controller;

import com.erp.erp_accounting.hr.payroll.dto.query.PayrollSearchCondition;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.service.PayrollQueryService;
import com.erp.erp_accounting.hr.payroll.service.command.SearchPayrollCommand;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
@Tag(name = "급여 조회", description = "급여 단건 및 목록 조회 API")
public class PayrollQueryController {

    private final PayrollQueryService payrollQueryService;

    @Operation(summary = "급여 단건 조회", description = "급여 ID로 급여 상세 정보를 조회합니다.")
    @GetMapping("/{payrollId}")
    public ResponseEntity<PayrollResponse> getPayroll(
            @Parameter(description = "급여 ID", required = true, example = "100")
            @PathVariable("payrollId") Long payrollId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(payrollQueryService.getPayroll(payrollId, principal));
    }

    @Operation(summary = "급여 목록 조회", description = "검색 조건과 페이징 정보를 기반으로 급여 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<PayrollListResponse>> getPayrollList(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "급여 검색 조건") @ModelAttribute PayrollSearchCondition condition,
            @Parameter(hidden = true) Pageable pageable
    ) {
        SearchPayrollCommand command = new SearchPayrollCommand(
                condition.getEmpNo(),
                condition.getEmpName(),
                condition.getDepartment(),
                condition.getPosition(),
                condition.getStatus(),
                condition.getPayMonth() != null ? YearMonth.parse(condition.getPayMonth()) : null,
                condition.getStartPayMonth() != null ? YearMonth.parse(condition.getStartPayMonth()) : null,
                condition.getEndPayMonth() != null ? YearMonth.parse(condition.getEndPayMonth()) : null,
                condition.getStartHireDate(),
                condition.getEndHireDate()
        );

        return ResponseEntity.ok(payrollQueryService.searchPayrolls(principal, command, pageable));
    }
}

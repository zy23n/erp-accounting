package com.erp.erp_accounting.hr.payroll.dto.query;

import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "급여 검색 조건 DTO")
public class PayrollSearchCondition {

    @Schema(description = "사번", example = "EMP001")
    private String empNo;

    @Schema(description = "사원명", example = "홍길동")
    private String empName;

    @Schema(description = "부서명", example = "개발팀")
    private String department;

    @Schema(description = "직급", example = "대리")
    private String position;

    @Schema(description = "급여 상태", example = "CONFIRMED")
    private PayrollStatus status;

    @Schema(description = "급여 월 (yyyy-MM)", example = "2026-01")
    private String payMonth;

    @Schema(description = "조회 시작 급여 월 (yyyy-MM)", example = "2025-10")
    private String startPayMonth;

    @Schema(description = "조회 종료 급여 월 (yyyy-MM)", example = "2026-01")
    private String endPayMonth;

    @Schema(description = "입사일 시작일", example = "2023-01-01")
    private LocalDate startHireDate;

    @Schema(description = "입사일 종료일", example = "2025-12-31")
    private LocalDate endHireDate;
}

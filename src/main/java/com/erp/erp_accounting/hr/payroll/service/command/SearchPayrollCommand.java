package com.erp.erp_accounting.hr.payroll.service.command;

import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Builder
@AllArgsConstructor
public class SearchPayrollCommand {
    private String empNo;
    private String empName;
    private String department;
    private String position;
    private PayrollStatus status;
    private YearMonth payMonth;
    private YearMonth startPayMonth;
    private YearMonth endPayMonth;
    private LocalDate startHireDate;
    private LocalDate endHireDate;
}

package com.erp.erp_accounting.hr.payroll.dto.query;

import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
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
public class PayrollSearchCondition {
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

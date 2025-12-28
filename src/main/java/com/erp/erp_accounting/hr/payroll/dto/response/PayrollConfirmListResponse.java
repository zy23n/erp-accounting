package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class PayrollConfirmListResponse {
    private Long id;
    private YearMonth payMonth;
    private PayrollConfirmStatus status;

    private Long confirmedById;
    private String confirmedByUsername;
    private LocalDateTime confirmedAt;
}
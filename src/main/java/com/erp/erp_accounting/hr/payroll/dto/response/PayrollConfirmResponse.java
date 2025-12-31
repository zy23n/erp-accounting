package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Getter
@AllArgsConstructor
public class PayrollConfirmResponse {
    private Long id;
    private YearMonth payMonth;
    private PayrollConfirmStatus status;

    private Long confirmedById;
    private String confirmedByUsername;
    private LocalDateTime confirmedAt;

    private Long canceledById;
    private String canceledByUsername;
    private LocalDateTime canceledAt;

    private List<PayrollResponse> payrolls;
}
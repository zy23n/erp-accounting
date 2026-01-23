package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
@AllArgsConstructor
public class PayrollConfirmListResponse {
    private Long id;
    private YearMonth payMonth;
    private PayrollConfirmStatus status;

    private Long confirmedById;
    private String confirmedByUsername;
    private LocalDateTime confirmedAt;

    public static PayrollConfirmListResponse fromEntity(PayrollConfirm confirm) {
        return PayrollConfirmListResponse.builder()
                .id(confirm.getId())
                .payMonth(confirm.getPayMonth())
                .status(confirm.getStatus())
                .confirmedById(getUserId(confirm.getConfirmedBy()))
                .confirmedByUsername(getUsername(confirm.getConfirmedBy()))
                .confirmedAt(confirm.getConfirmedAt())
                .build();
    }
}
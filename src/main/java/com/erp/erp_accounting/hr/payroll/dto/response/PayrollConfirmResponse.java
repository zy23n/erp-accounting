package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
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

    public static PayrollConfirmResponse fromEntity(PayrollConfirm confirm) {
        List<PayrollResponse> payrolls = confirm.getPayrolls().stream()
                .map(PayrollResponse::fromEntity)
                .toList();

        return PayrollConfirmResponse.builder()
                .id(confirm.getId())
                .payMonth(confirm.getPayMonth())
                .status(confirm.getStatus())
                .confirmedById(getUserId(confirm.getConfirmedBy()))
                .confirmedByUsername(getUsername(confirm.getConfirmedBy()))
                .confirmedAt(confirm.getConfirmedAt())
                .canceledById(getUserId(confirm.getCanceledBy()))
                .canceledByUsername(getUsername(confirm.getCanceledBy()))
                .canceledAt(confirm.getCanceledAt())
                .payrolls(payrolls)
                .build();
    }
}
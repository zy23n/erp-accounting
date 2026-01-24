package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "급여 확정 상세 응답 DTO")
public class PayrollConfirmResponse {

    @Schema(description = "급여 확정 ID", example = "1")
    private Long id;

    @Schema(description = "급여 월 (yyyy-MM)", example = "2026-01")
    private YearMonth payMonth;

    @Schema(description = "급여 확정 상태", example = "CONFIRMED")
    private PayrollConfirmStatus status;

    @Schema(description = "확정 처리 사용자 ID", example = "1")
    private Long confirmedById;

    @Schema(description = "확정 처리 사용자 계정명", example = "hr_admin")
    private String confirmedByUsername;

    @Schema(description = "확정 처리 일시", example = "2026-01-31T18:00:00")
    private LocalDateTime confirmedAt;

    @Schema(description = "취소 처리 사용자 ID", example = "2")
    private Long canceledById;

    @Schema(description = "취소 처리 사용자 계정명", example = "hr_manager")
    private String canceledByUsername;

    @Schema(description = "취소 처리 일시", example = "2026-02-01T10:30:00")
    private LocalDateTime canceledAt;

    @Schema(description = "급여 내역 리스트")
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
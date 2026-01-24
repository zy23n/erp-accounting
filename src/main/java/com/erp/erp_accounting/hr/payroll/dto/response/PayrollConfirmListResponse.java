package com.erp.erp_accounting.hr.payroll.dto.response;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "급여 확정 목록 응답 DTO")
public class PayrollConfirmListResponse {

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
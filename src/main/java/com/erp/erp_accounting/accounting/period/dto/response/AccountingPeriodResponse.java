package com.erp.erp_accounting.accounting.period.dto.response;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriodStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회계기간 마감 상태 응답 DTO")
public class AccountingPeriodResponse {

    @Schema(description = "회계기간 (yyyy-MM)", example = "2026-01")
    private String period;

    @Schema(description = "회계기간 상태", example = "CLOSED")
    private AccountingPeriodStatus status;

    @Schema(description = "마감 처리 사용자 ID", example = "1")
    private Long closedById;

    @Schema(description = "마감 처리 사용자 계정명", example = "admin")
    private String closedByUsername;

    @Schema(description = "마감 처리 일시", example = "2026-01-31T23:59:59")
    private LocalDateTime closedAt;

    @Schema(description = "마감 취소 사용자 ID", example = "1")
    private Long reopenedById;

    @Schema(description = "마감 취소 사용자 계정명", example = "admin")
    private String reopenedByUsername;

    @Schema(description = "마감 취소 일시", example = "2026-02-01T09:00:00")
    private LocalDateTime reopenedAt;

    public static AccountingPeriodResponse fromEntity(AccountingPeriod ap) {
        return AccountingPeriodResponse.builder()
                .period(ap.getPeriod().toString())
                .status(ap.getStatus())
                .closedById(getUserId(ap.getClosedBy()))
                .closedByUsername(getUsername(ap.getClosedBy()))
                .closedAt(ap.getClosedAt())
                .reopenedById(getUserId(ap.getReopenedBy()))
                .reopenedByUsername(getUsername(ap.getReopenedBy()))
                .reopenedAt(ap.getReopenedAt())
                .build();
    }
}

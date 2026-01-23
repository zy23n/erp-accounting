package com.erp.erp_accounting.accounting.period.dto.response;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
@AllArgsConstructor
public class AccountingPeriodResponse {
    private String period;
    private String status;
    private Long closedById;
    private String closedByUsername;
    private LocalDateTime closedAt;
    private Long reopenedById;
    private String reopenedByUsername;
    private LocalDateTime reopenedAt;

    public static AccountingPeriodResponse fromEntity(AccountingPeriod ap) {
        return AccountingPeriodResponse.builder()
                .period(ap.getPeriod().toString())
                .status(ap.getStatus().name())
                .closedById(getUserId(ap.getClosedBy()))
                .closedByUsername(getUsername(ap.getClosedBy()))
                .closedAt(ap.getClosedAt())
                .reopenedById(getUserId(ap.getReopenedBy()))
                .reopenedByUsername(getUsername(ap.getReopenedBy()))
                .reopenedAt(ap.getReopenedAt())
                .build();
    }
}

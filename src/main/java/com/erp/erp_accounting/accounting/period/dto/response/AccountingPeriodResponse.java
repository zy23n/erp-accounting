package com.erp.erp_accounting.accounting.period.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AccountingPeriodResponse {
    private String period;
    private String status;
    private String closedByUsername;
    private LocalDateTime closedAt;
    private String reopenedByUsername;
    private LocalDateTime reopenedAt;
}

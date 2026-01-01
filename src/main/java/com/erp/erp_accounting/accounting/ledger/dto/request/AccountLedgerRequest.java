package com.erp.erp_accounting.accounting.ledger.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountLedgerRequest {
    private Long accountId;
    private LocalDate startDate;
    private LocalDate endDate;
}

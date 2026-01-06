package com.erp.erp_accounting.accounting.balance.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountAmountDto {
    private Long accountId;
    private BigDecimal amount;
}

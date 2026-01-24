package com.erp.erp_accounting.accounting.ledger.service.command;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class MonthlyAccountBalanceCommand {
    private final Long accountId;
    private final YearMonth month;

    public void validate() {
        if (month.isAfter(YearMonth.now())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    String.format("조회 월 초과 (month=%s, now=%s)", month, YearMonth.now())
            );
        }
    }
}

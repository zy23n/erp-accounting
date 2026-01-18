package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountingPeriodValidator {

    // 마감/전표/급여 등 처리 가능 여부 검증
    public void assertNotClosed(AccountingPeriod period) {
        if (period.isClosed()) {
            throw new BusinessException(ErrorCode.PERIOD_ALREADY_CLOSED,
                    String.format("이미 마감된 회계기간 (period=%s)", period.getPeriod()));
        }
    }

    // 재마감 가능 여부 검증
    public void assertCanReopen(AccountingPeriod period) {
        if (!period.isClosed()) {
            throw new BusinessException(ErrorCode.PERIOD_NOT_CLOSED,
                    String.format("아직 마감되지 않은 회계기간 (period=%s)", period.getPeriod()));
        }
    }
}

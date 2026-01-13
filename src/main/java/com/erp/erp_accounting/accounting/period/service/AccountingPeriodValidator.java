package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountingPeriodValidator {

    // 마감 가능 여부 검증
    public void assertClosable(AccountingPeriod period) {
        if (period.isClosed()) {
            throw new BusinessException(ErrorCode.PERIOD_ALREADY_CLOSED);
        }
    }

    // 재마감 가능 여부 검증
    public void assertReopenable(AccountingPeriod period) {
        if (!period.isClosed()) {
            throw new BusinessException(ErrorCode.INVALID_STATE);
        }
    }

    // 전표 입력/승인 등 마감 여부 검증
    public void assertPeriodOpen(AccountingPeriod period) {
        if (period.isClosed()) {
            throw new BusinessException(ErrorCode.PERIOD_ALREADY_CLOSED);
        }
    }
}

package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountingPeriodValidator {

    // 마감 가능 여부 검증
    public void assertClosable(AccountingPeriod period) {
        if (period.isClosed()) {
            throw new IllegalStateException("이미 마감된 기간");
        }
    }

    // 재마감 가능 여부 검증
    public void assertReopenable(AccountingPeriod period) {
        if (!period.isClosed()) {
            throw new IllegalStateException("마감 상태가 아닌 기간은 재마감 불가");
        }
    }

    // 전표 입력/승인 등 마감 여부 검증
    public void assertPeriodOpen(AccountingPeriod period) {
        if (period.isClosed()) {
            throw new IllegalStateException("마감된 기간에는 데이터 변경 불가");
        }
    }
}

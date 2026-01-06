package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.repository.AccountingPeriodRepository;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountingPeriodService {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingPeriodValidator validator;

    // 특정 회계기간 조회
    public AccountingPeriod getByPeriod(YearMonth period) {
        return accountingPeriodRepository.findByPeriod(period)
                .orElseThrow(() -> new IllegalStateException("회계기간 없음: " + period));
    }

    // 마감 가능한 회계기간 조회
    public AccountingPeriod getClosablePeriodOrThrow(YearMonth period) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertClosable(accountingPeriod);
        return accountingPeriod;
    }

    // 회계기간 사전 생성 (연초 / 초기화용)
    @Transactional
    public AccountingPeriod getOrCreate(YearMonth period) {
        return accountingPeriodRepository.findByPeriod(period)
                .orElseGet(() -> accountingPeriodRepository.save(AccountingPeriod.open(period)));
    }

    // 연 단위 회계기간 일괄 생성
    @Transactional
    public void createYear(int year) {
        for (int month = 1; month <= 12; month++) {
            getOrCreate(YearMonth.of(year, month));
        }
    }

    // 마감 여부 검증 (전표/급여 확정 공용)
    public void assertPeriodOpen(YearMonth period) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertPeriodOpen(accountingPeriod);
    }

    @Transactional
    public void close(YearMonth period, User user) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertClosable(accountingPeriod);
        accountingPeriod.close(user);
    }

    @Transactional
    public void reopen(YearMonth period, User user) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertReopenable(accountingPeriod);
        accountingPeriod.reopen(user);
    }
}

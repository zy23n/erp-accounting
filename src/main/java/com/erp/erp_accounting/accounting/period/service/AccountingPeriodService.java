package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.repository.AccountingPeriodRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("회계기간 미존재 (period=%s)", period)));
    }

    // 마감 가능한 회계기간 조회
    public AccountingPeriod getPeriodForClosing(YearMonth period) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertNotClosed(accountingPeriod);
        return accountingPeriod;
    }

    // 마감 여부 확인
    @Cacheable(value = "accountingPeriod:closed", key = "#p0")
    public boolean isPeriodClosed(YearMonth period) {
        return accountingPeriodRepository.findByPeriod(period)
                .map(AccountingPeriod::isClosed)
                .orElse(true);
    }

    // 마감 여부 검증
    public void assertPeriodOpen(YearMonth period) {
        if (isPeriodClosed(period)) {
            throw new BusinessException(
                    ErrorCode.PERIOD_ALREADY_CLOSED, String.format("이미 마감된 회계기간 (period=%s)", period)
            );
        }
    }

    // 이전 월 마감 여부 확인
    public boolean isPreviousPeriodClosed(YearMonth period) {
        return isPeriodClosed(period.minusMonths(1));
    }

    // 이전 월 마감 여부 검증
    public void assertPreviousPeriodClosed(YearMonth period) {
        YearMonth prev = period.minusMonths(1);
        if (!isPeriodClosed(prev)) {
            throw new BusinessException(ErrorCode.PERIOD_NOT_CLOSED,
                    String.format("이전 회계기간 미마감 (period=%s)", period.minusMonths(1)));
        }
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

    @Transactional
    public void close(YearMonth period, User closer) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertNotClosed(accountingPeriod);
        accountingPeriod.close(closer);
    }

    @Transactional
    public void reopen(YearMonth period, User reopener) {
        AccountingPeriod accountingPeriod = getByPeriod(period);
        validator.assertCanReopen(accountingPeriod);
        accountingPeriod.reopen(reopener);
    }
}

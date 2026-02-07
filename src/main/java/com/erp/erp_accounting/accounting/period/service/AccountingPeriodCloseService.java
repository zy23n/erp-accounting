package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.balance.dto.query.AccountAmountDto;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.balance.service.MonthlyBalanceCalculationService;
import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountingPeriodCloseService {

    private final AccountingPeriodService accountingPeriodService;
    private final MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;
    private final MonthlyBalanceCalculationService balanceCalculationService;
    private final AccountRepository accountRepository;
    private final VoucherLineRepository voucherLineRepository;

    // 회계기간 마감
    @CacheEvict(value = "accountingPeriod:closed", key = "#p0")
    public AccountingPeriodResponse closePeriod(YearMonth period, User closer) {

        log.info("[ACCOUNTING_PERIOD] action=CLOSE_REQUEST, period={}, closerId={}", period, closer.getId());

        // 마감 가능 여부 검증
        accountingPeriodService.assertPreviousPeriodClosed(period);
        AccountingPeriod accountingPeriod = accountingPeriodService.getPeriodForClosing(period);

        // 마감 대상 계정 조회 (말단 계정만)
        List<Account> leafAccounts = accountRepository.findLeafAccounts();

        // 이전 월 마감 잔액 조회 (스냅샷 기준)
        Map<Long, BigDecimal> openingBalances = loadPreviousClosingBalances(period.minusMonths(1));

        // 이번 달 차변 / 대변 합계 (원장 기준)
        Map<Long, BigDecimal> debitSums = loadMonthlySums(period, LineType.DEBIT);
        Map<Long, BigDecimal> creditSums = loadMonthlySums(period, LineType.CREDIT);

        // 대차검증
        balanceCalculationService.validateMonthlyTrialBalance(debitSums, creditSums, period);

        // 월별 잔액 스냅샷 계산
        List<MonthlyAccountBalance> balances =
                balanceCalculationService.calculateMonthlyBalances(
                        leafAccounts, openingBalances, debitSums, creditSums, period);

        // 기존 스냅샷 제거 및 저장
        monthlyAccountBalanceRepository.deleteByPeriod(period);
        monthlyAccountBalanceRepository.saveAll(balances);

        // 회계기간 상태 변경
        accountingPeriodService.close(period, closer);

        log.info("[ACCOUNTING_PERIOD] action=CLOSE_COMPLETE, period={}, closerId={}, snapshotCount={}",
                period, closer.getId(), balances.size());

        return AccountingPeriodResponse.fromEntity(accountingPeriod);
    }

    // 회계기간 마감 취소
    @CacheEvict(value = "accountingPeriod:closed", key = "#p0")
    public AccountingPeriodResponse reopenPeriod(YearMonth period, User reopener) {

        log.info("[ACCOUNTING_PERIOD] action=REOPEN_REQUEST, period={}, reopenerId={}", period, reopener.getId());

        // 스냅샷 제거
        monthlyAccountBalanceRepository.deleteByPeriod(period);

        // 회계기간 상태 변경
        accountingPeriodService.reopen(period, reopener);

        AccountingPeriod reopenedPeriod = accountingPeriodService.getByPeriod(period);

        log.info("[ACCOUNTING_PERIOD] action=REOPEN_COMPLETE, period={}, reopenerId={}", period, reopener.getId());

        return AccountingPeriodResponse.fromEntity(reopenedPeriod);
    }

    private Map<Long, BigDecimal> loadPreviousClosingBalances(YearMonth previousPeriod) {
        return monthlyAccountBalanceRepository.findByPeriod(previousPeriod)
                .stream()
                .collect(Collectors.toMap(
                        mab -> mab.getAccount().getId(),
                        MonthlyAccountBalance::getClosingBalance
                ));
    }

    private Map<Long, BigDecimal> loadMonthlySums(YearMonth period, LineType type) {
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        return (type == LineType.DEBIT
                ? voucherLineRepository.findMonthlyDebitSum(start, end)
                : voucherLineRepository.findMonthlyCreditSum(start, end))
                .stream()
                .collect(Collectors.toMap(
                        AccountAmountDto::getAccountId,
                        AccountAmountDto::getAmount
                ));
    }
}
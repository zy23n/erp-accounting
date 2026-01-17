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
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public AccountingPeriodResponse closePeriod(YearMonth period, User closer) {

        // 마감 가능 여부 검증
        accountingPeriodService.assertPreviousPeriodClosed(period);
        AccountingPeriod accountingPeriod = accountingPeriodService.getClosablePeriodOrThrow(period);

        // 마감 대상 계정 조회 (말단 계정만)
        List<Account> leafAccounts = accountRepository.findLeafAccounts();

        // 이전 월 마감 잔액 조회 (스냅샷 기준)
        Map<Long, BigDecimal> openingBalances =
                loadPreviousClosingBalances(period.minusMonths(1));

        // 이번 달 차변 / 대변 합계 (원장 기준)
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();
        Map<Long, BigDecimal> debitSums = loadMonthlyDebitSums(start, end);
        Map<Long, BigDecimal> creditSums = loadMonthlyCreditSums(start, end);

        // 대차검증
        validateMonthlyTrialBalance(debitSums, creditSums, period);

        // 월별 잔액 스냅샷 계산
        List<MonthlyAccountBalance> balances =
                balanceCalculationService.calculateMonthlyBalances(
                        leafAccounts, openingBalances, debitSums, creditSums, period);

        // 기존 스냅샷 제거 및 저장
        monthlyAccountBalanceRepository.deleteByPeriod(period);
        monthlyAccountBalanceRepository.saveAll(balances);

        // 회계기간 상태 변경
        accountingPeriodService.close(period, closer);

        log.info("[PERIOD_CLOSED] period={}, closedBy={}, snapshotCount={}", period, closer.getId(), balances.size());

        return toResponse(accountingPeriod);
    }

    // 회계기간 마감 취소
    public AccountingPeriodResponse reopenPeriod(YearMonth period, User reopener) {

        // 스냅샷 제거
        monthlyAccountBalanceRepository.deleteByPeriod(period);

        // 회계기간 상태 변경
        accountingPeriodService.reopen(period, reopener);

        AccountingPeriod reopenedPeriod = accountingPeriodService.getByPeriod(period);

        log.info("[PERIOD_REOPENED] period={}, reopenedBy={}", period, reopener.getId());

        return toResponse(reopenedPeriod);
    }

    private Map<Long, BigDecimal> loadPreviousClosingBalances(YearMonth previousPeriod) {
        return monthlyAccountBalanceRepository.findByPeriod(previousPeriod)
                .stream()
                .collect(Collectors.toMap(
                        mab -> mab.getAccount().getId(),
                        MonthlyAccountBalance::getClosingBalance
                ));
    }

    private Map<Long, BigDecimal> loadMonthlyDebitSums(LocalDate start, LocalDate end) {
        return voucherLineRepository.findMonthlyDebitSum(start, end)
                .stream()
                .collect(Collectors.toMap(
                        AccountAmountDto::getAccountId,
                        AccountAmountDto::getAmount
                ));
    }

    private void validateMonthlyTrialBalance(
            Map<Long, BigDecimal> debitSums, Map<Long, BigDecimal> creditSums, YearMonth period
    ) {
        BigDecimal totalDebit = debitSums.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCredit = creditSums.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException(ErrorCode.IMBALANCE_AMOUNT);
        }
    }

    private Map<Long, BigDecimal> loadMonthlyCreditSums(LocalDate start, LocalDate end) {
        return voucherLineRepository.findMonthlyCreditSum(start, end)
                .stream()
                .collect(Collectors.toMap(
                        AccountAmountDto::getAccountId,
                        AccountAmountDto::getAmount
                ));
    }

    private AccountingPeriodResponse toResponse(AccountingPeriod ap) {
        return new AccountingPeriodResponse(
                ap.getPeriod().toString(),
                ap.getStatus().name(),
                ap.getClosedBy() != null ? ap.getClosedBy().getUsername() : null,
                ap.getClosedAt(),
                ap.getReopenedBy() != null ? ap.getReopenedBy().getUsername() : null,
                ap.getReopenedAt()
        );
    }
}
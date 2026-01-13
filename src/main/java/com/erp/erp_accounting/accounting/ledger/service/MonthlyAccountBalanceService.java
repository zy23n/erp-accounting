package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.request.MonthlyAccountBalanceRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.common.BalanceCalculator;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyAccountBalanceService {

    private final VoucherLineRepository voucherLineRepository;
    private final AccountRepository accountRepository;
    private final AccountingPeriodService accountingPeriodService;
    private final MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;

    public MonthlyAccountBalanceResponse getMonthlyBalance(MonthlyAccountBalanceRequest request) {
        return accountingPeriodService.isClosed(request.getMonth())
                ? getFromSnapshot(request)
                : getRealtimeBalance(request);
    }

    // 마감 월: 스냅샷
    private MonthlyAccountBalanceResponse getFromSnapshot(MonthlyAccountBalanceRequest request) {

        MonthlyAccountBalance balance =
                monthlyAccountBalanceRepository
                        .findByPeriodAndAccountId(request.getMonth(), request.getAccountId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return new MonthlyAccountBalanceResponse(
                balance.getOpeningBalance(),
                balance.getDebitSum(),
                balance.getCreditSum(),
                balance.getClosingBalance()
        );
    }

    // 미마감 월: 실시간 계산
    private MonthlyAccountBalanceResponse getRealtimeBalance(MonthlyAccountBalanceRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        NormalBalance normalBalance = account.getNormalBalance();

        LocalDate monthStart = request.getMonth().atDay(1);
        LocalDate monthEnd = request.getMonth().atEndOfMonth();

        // 전기이월
        BigDecimal openingBalance = resolveOpeningBalance(request.getMonth(), request.getAccountId(), normalBalance);

        // 이번 달 총합
        MonthlyBalanceQueryDto monthly = voucherLineRepository.findMonthlyTotal(request.getAccountId(), monthStart, monthEnd);

        BigDecimal debit = monthly.getDebitSum();
        BigDecimal credit = monthly.getCreditSum();

        BigDecimal closingBalance = BalanceCalculator.applyNormalBalance(normalBalance, openingBalance, debit, credit);

        return new MonthlyAccountBalanceResponse(
                openingBalance,
                debit,
                credit,
                closingBalance
        );
    }

    // 전기이월 계산 정책: 이전 월 마감 시 스냅샷, 미마감 시 실시간 누적
    private BigDecimal resolveOpeningBalance(YearMonth month, Long accountId, NormalBalance normalBalance) {
        return accountingPeriodService.isPreviousPeriodClosed(month)
                ? getSnapshotClosingBalance(month.minusMonths(1), accountId)
                : calculateRealtimeOpeningBalance(month, accountId, normalBalance);
    }

    private BigDecimal getSnapshotClosingBalance(YearMonth prev, Long accountId) {
        return monthlyAccountBalanceRepository
                .findByPeriodAndAccountId(prev, accountId)
                .map(MonthlyAccountBalance::getClosingBalance)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateRealtimeOpeningBalance(YearMonth month, Long accountId, NormalBalance normalBalance) {

        LocalDate monthStart = month.atDay(1);

        OpeningBalanceDto opening = voucherLineRepository.findOpeningBalance(accountId, monthStart);

        return BalanceCalculator.applyNormalBalance(
                normalBalance, BigDecimal.ZERO, opening.getDebitSum(), opening.getCreditSum());
    }
}

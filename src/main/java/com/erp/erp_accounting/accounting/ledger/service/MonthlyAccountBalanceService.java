package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.common.BalanceCalculator;
import com.erp.erp_accounting.accounting.ledger.service.command.MonthlyAccountBalanceCommand;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyAccountBalanceService {

    private final VoucherLineRepository voucherLineRepository;
    private final AccountRepository accountRepository;
    private final AccountingPeriodService accountingPeriodService;
    private final MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;

    public MonthlyAccountBalanceResponse getMonthlyBalance(MonthlyAccountBalanceCommand command) {
        boolean closed = accountingPeriodService.isClosed(command.getMonth());
        log.info("[MONTHLY_BALANCE] action=QUERY, accountId={}, month={}, closed={}",
                command.getAccountId(), command.getMonth(), closed);
        return closed ? getFromSnapshot(command) : getRealtimeBalance(command);
    }

    // 마감 월: 스냅샷
    private MonthlyAccountBalanceResponse getFromSnapshot(MonthlyAccountBalanceCommand command) {
        MonthlyAccountBalance balance =
                monthlyAccountBalanceRepository
                        .findByPeriodAndAccountId(command.getMonth(), command.getAccountId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                String.format("월별 잔액 스냅샷 미존재 (period=%s, accountId=%d)",
                                        command.getMonth(), command.getAccountId())));

        return MonthlyAccountBalanceResponse.fromSnapshot(balance);
    }

    // 미마감 월: 실시간 계산
    private MonthlyAccountBalanceResponse getRealtimeBalance(MonthlyAccountBalanceCommand command) {
        Account account = accountRepository.findById(command.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("계정과목 미존재 (accountId=%d)", command.getAccountId())));

        NormalBalance normalBalance = account.getNormalBalance();

        LocalDate monthStart = command.getMonth().atDay(1);
        LocalDate monthEnd = command.getMonth().atEndOfMonth();

        // 전기이월
        BigDecimal openingBalance = resolveOpeningBalance(command.getMonth(), command.getAccountId(), normalBalance);

        // 이번 달 총합
        MonthlyBalanceQueryDto monthly = voucherLineRepository.findMonthlyTotal(command.getAccountId(), monthStart, monthEnd);

        BigDecimal debit = monthly.getDebitSum();
        BigDecimal credit = monthly.getCreditSum();

        BigDecimal closingBalance = BalanceCalculator.applyNormalBalance(normalBalance, openingBalance, debit, credit);

        return MonthlyAccountBalanceResponse.fromRealtime(openingBalance, debit, credit, closingBalance);
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

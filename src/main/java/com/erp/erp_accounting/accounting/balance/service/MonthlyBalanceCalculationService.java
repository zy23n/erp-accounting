package com.erp.erp_accounting.accounting.balance.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.common.BalanceCalculator;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonthlyBalanceCalculationService {

    // 원장 데이터를 기반으로 월별 계정별 잔액 계산
    public List<MonthlyAccountBalance> calculateMonthlyBalances(
            List<Account> accounts,
            Map<Long, BigDecimal> openingBalances,
            Map<Long, BigDecimal> debitSums,
            Map<Long, BigDecimal> creditSums,
            YearMonth period
    ) {
        List<MonthlyAccountBalance> result = new ArrayList<>();

        for (Account account : accounts) {
            if (!account.isLeaf()) continue;

            Long accountId = account.getId();

            BigDecimal opening = openingBalances.getOrDefault(accountId, BigDecimal.ZERO);
            BigDecimal debit = debitSums.getOrDefault(accountId, BigDecimal.ZERO);
            BigDecimal credit = creditSums.getOrDefault(accountId, BigDecimal.ZERO);

            if (isZero(opening) && isZero(debit) && isZero(credit)) continue;

            BigDecimal closing = BalanceCalculator.applyNormalBalance(account.getNormalBalance(), opening, debit, credit);

            result.add(
                    MonthlyAccountBalance.builder()
                            .account(account)
                            .period(period)
                            .openingBalance(opening)
                            .debitSum(debit)
                            .creditSum(credit)
                            .closingBalance(closing)
                            .normalBalance(account.getNormalBalance())
                            .build()
            );
        }

        return result;
    }

    private boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    // 전기이월
    public BigDecimal calculateOpeningBalance(
            NormalBalance normalBalance,
            BigDecimal debit,
            BigDecimal credit
    ) {
        return BalanceCalculator.applyNormalBalance(normalBalance, BigDecimal.ZERO, debit, credit);
    }

    // 기말잔액
    public BigDecimal calculateClosingBalance(
            NormalBalance normalBalance,
            BigDecimal opening,
            BigDecimal debit,
            BigDecimal credit
    ) {
        return BalanceCalculator.applyNormalBalance(normalBalance, opening, debit, credit);
    }

    // 대차검증
    public void validateMonthlyTrialBalance(
            Map<Long, BigDecimal> debitSums,
            Map<Long, BigDecimal> creditSums,
            YearMonth period
    ) {
        BigDecimal totalDebit = debitSums.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = creditSums.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException(ErrorCode.IMBALANCE_AMOUNT,
                    String.format("월별 잔액 불일치 (회계기간=%s, 차변 합계=%s, 대변 합계=%s)", period, totalDebit, totalCredit));
        }
    }
}

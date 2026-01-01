package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.request.MonthlyAccountBalanceRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.global.util.BalanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyAccountBalanceService {

    private final VoucherLineRepository voucherLineRepository;
    private final AccountRepository accountRepository;

    public MonthlyAccountBalanceResponse getMonthlyBalance(MonthlyAccountBalanceRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("계정과목 없음"));

        NormalBalance normalBalance = account.getNormalBalance();

        LocalDate monthStart = request.getMonth().atDay(1);
        LocalDate monthEnd = request.getMonth().atEndOfMonth();

        // 전기이월
        OpeningBalanceDto opening = voucherLineRepository.findOpeningBalance(
                request.getAccountId(), monthStart);

        // 이번 달 총합
        MonthlyBalanceQueryDto monthly = voucherLineRepository.findMonthlyTotal(
                request.getAccountId(), monthStart, monthEnd);

        // 잔액 계산
        BigDecimal openingBalance = BalanceCalculator.applyNormalBalance(
                normalBalance, BigDecimal.ZERO, opening.getDebitSum(), opening.getCreditSum());

        BigDecimal debit = monthly.getDebitSum() != null ? monthly.getDebitSum() : BigDecimal.ZERO;
        BigDecimal credit = monthly.getCreditSum() != null ? monthly.getCreditSum() : BigDecimal.ZERO;

        BigDecimal closingBalance = BalanceCalculator.applyNormalBalance(
                normalBalance, openingBalance, debit, credit);

        return new MonthlyAccountBalanceResponse(
                openingBalance,
                monthly.getDebitSum(),
                monthly.getCreditSum(),
                closingBalance
        );
    }
}

package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.repository.AccountLedgerQueryRepository;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.common.BalanceCalculator;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountLedgerService {

    private final VoucherLineRepository voucherLineRepository;
    private final AccountRepository accountRepository;
    private final AccountLedgerQueryRepository accountLedgerQueryRepository;

    public AccountLedgerResponse searchAccountLedger(AccountLedgerSearchCondition condition) {

        validateCondition(condition);

        Account account = accountRepository.findById(condition.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("계정과목 미존재 (accountId=%d)", condition.getAccountId())));

        NormalBalance normalBalance = account.getNormalBalance();

        // 전기이월 잔액 조회
        OpeningBalanceDto ob = voucherLineRepository.findOpeningBalance(
                condition.getAccountId(), condition.getStartDateOrDefault()
        );

        BigDecimal openingBalance = BalanceCalculator.applyNormalBalance(
                normalBalance, BigDecimal.ZERO, ob.getDebitSum(), ob.getCreditSum());

        // 기간 내 원장 조회
        List<AccountLedgerItemDto> queryDtos = accountLedgerQueryRepository.searchLedger(condition);

        List<AccountLedgerItemDto> items = new ArrayList<>();
        BigDecimal balance = openingBalance;

        // 거래 row
        for (AccountLedgerItemDto dto : queryDtos) {

            BigDecimal debit = dto.getDebitAmount() != null ? dto.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = dto.getCreditAmount() != null ? dto.getCreditAmount() : BigDecimal.ZERO;

            balance = BalanceCalculator.applyNormalBalance(normalBalance, balance, debit, credit);

            items.add(new AccountLedgerItemDto(
                    dto.getVoucherDate(),
                    dto.getVoucherNo(),
                    dto.getDescription(),
                    debit,
                    credit,
                    balance
            ));
        }

        BigDecimal closingBalance = balance;

        return new AccountLedgerResponse(openingBalance, closingBalance, items);
    }

    private void validateCondition(AccountLedgerSearchCondition condition) {
        LocalDate start = condition.getStartDateOrDefault();
        LocalDate end = condition.getEndDateOrDefault();

        if (end.isBefore(start)) throw new BusinessException(ErrorCode.INVALID_REQUEST, "조회 기간 범위 오류 (startDate > endDate)");

        long days = ChronoUnit.DAYS.between(start, end);
        if (days > 365) throw new BusinessException(ErrorCode.INVALID_REQUEST, "조회 기간은 최대 1년까지 가능");
    }
}
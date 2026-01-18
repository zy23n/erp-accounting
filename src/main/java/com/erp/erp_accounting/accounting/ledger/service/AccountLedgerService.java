package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.request.AccountLedgerRequest;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerItemDto;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.common.BalanceCalculator;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountLedgerService {

    private final VoucherLineRepository voucherLineRepository;
    private final AccountRepository accountRepository;

    private static final String OPENING_ROW = "OPENING";
    private static final String TOTAL_ROW = "TOTAL";

    public AccountLedgerResponse getAccountLedger(AccountLedgerRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("계정과목 미존재 (accountId=%d)", request.getAccountId())));

        NormalBalance normalBalance = account.getNormalBalance();

        // 전기이월 잔액 조회
        OpeningBalanceDto ob = voucherLineRepository.findOpeningBalance(
                request.getAccountId(),
                request.getStartDate()
        );

        BigDecimal openingBalance = BalanceCalculator.applyNormalBalance(
                normalBalance, BigDecimal.ZERO, ob.getDebitSum(), ob.getCreditSum());

        // 기간 내 원장 조회
        List<AccountLedgerQueryDto> queryDtos =
                voucherLineRepository.findAccountLedger(
                        request.getAccountId(), request.getStartDate(), request.getEndDate());

        List<AccountLedgerItemDto> items = new ArrayList<>();
        BigDecimal balance = openingBalance;

        // 전기이월 row
        items.add(new AccountLedgerItemDto(
                null,
                OPENING_ROW,
                "전기이월",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                balance
        ));

        // 거래 row + 누계
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (AccountLedgerQueryDto dto : queryDtos) {

            BigDecimal debit = dto.getDebitAmount() != null ? dto.getDebitAmount() : BigDecimal.ZERO;
            BigDecimal credit = dto.getCreditAmount() != null ? dto.getCreditAmount() : BigDecimal.ZERO;

            balance = BalanceCalculator.applyNormalBalance(normalBalance, balance, debit, credit);

            totalDebit = totalDebit.add(debit);
            totalCredit = totalCredit.add(credit);

            items.add(new AccountLedgerItemDto(
                    dto.getVoucherDate(),
                    dto.getVoucherNo(),
                    dto.getDescription(),
                    debit,
                    credit,
                    balance
            ));
        }

        // 합계 row
        items.add(new AccountLedgerItemDto(
                null,
                TOTAL_ROW,
                "합계",
                totalDebit,
                totalCredit,
                balance
        ));

        // 마감 잔액
        BigDecimal closingBalance = balance;

        // 최종 응답
        return new AccountLedgerResponse(openingBalance, closingBalance, items);
    }
}
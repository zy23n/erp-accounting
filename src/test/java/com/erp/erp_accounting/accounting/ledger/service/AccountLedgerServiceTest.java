package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.ledger.dto.response.AccountLedgerResponse;
import com.erp.erp_accounting.accounting.ledger.repository.AccountLedgerQueryRepository;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.fixture.AccountFixture;
import com.erp.erp_accounting.fixture.LedgerItemFixture;
import com.erp.erp_accounting.fixture.Money;
import com.erp.erp_accounting.fixture.OpeningBalanceFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("계정별 원장 서비스 테스트")
class AccountLedgerServiceTest {

    @InjectMocks
    private AccountLedgerService accountLedgerService;

    @Mock
    private VoucherLineRepository voucherLineRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountLedgerQueryRepository accountLedgerQueryRepository;

    private static final Long ACCOUNT_ID = 1L;
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END   = LocalDate.of(2026, 1, 31);

    @Test
    @DisplayName("계정별 원장 조회 성공 - 전기이월 + 거래 누적 + 잔액 계산")
    void searchAccountLedger_success() {
        // given
        AccountLedgerSearchCondition condition = new AccountLedgerSearchCondition(ACCOUNT_ID, START, END);

        Account account = AccountFixture.cash();
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(account));

        // 전기이월: 차변 1,000 / 대변 200 → opening = 800
        OpeningBalanceDto openingBalanceDto = OpeningBalanceFixture.of("1000", "200");

        given(voucherLineRepository.findOpeningBalance(eq(ACCOUNT_ID), any(LocalDate.class))).willReturn(openingBalanceDto);

        // 기간 내 거래
        AccountLedgerItemDto deposit =
                LedgerItemFixture.deposit(
                        LocalDate.of(2026, 1, 5),
                        "V001",
                        "입금",
                        "500"
                );

        AccountLedgerItemDto withdraw =
                LedgerItemFixture.withdraw(
                        LocalDate.of(2026, 1, 20),
                        "V002",
                        "출금",
                        "300"
                );

        given(accountLedgerQueryRepository.searchLedger(condition)).willReturn(List.of(deposit, withdraw));

        // when
        AccountLedgerResponse response = accountLedgerService.searchAccountLedger(condition);

        // then
        // opening
        assertThat(response.getOpeningBalance()).isEqualByComparingTo("800");

        // ledger items
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems())
                .extracting(AccountLedgerItemDto::getBalance)
                .containsExactly(
                        Money.of("1300"), // 800 + 500
                        Money.of("1000")  // 1300 - 300
                );

        // closing
        assertThat(response.getClosingBalance()).isEqualByComparingTo("1000");

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(voucherLineRepository).findOpeningBalance(eq(ACCOUNT_ID), any(LocalDate.class));
        verify(accountLedgerQueryRepository).searchLedger(condition);
    }
}
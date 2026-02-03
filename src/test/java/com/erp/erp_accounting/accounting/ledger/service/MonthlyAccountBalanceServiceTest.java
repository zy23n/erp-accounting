package com.erp.erp_accounting.accounting.ledger.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.balance.service.MonthlyBalanceCalculationService;
import com.erp.erp_accounting.accounting.ledger.dto.response.MonthlyAccountBalanceResponse;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.ledger.service.command.MonthlyAccountBalanceCommand;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.fixture.AccountFixture;
import com.erp.erp_accounting.fixture.Money;
import com.erp.erp_accounting.fixture.MonthlyAccountBalanceFixture;
import com.erp.erp_accounting.fixture.MonthlyBalanceQueryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("월별 계정 잔액 서비스 테스트")
class MonthlyAccountBalanceServiceTest {

    @InjectMocks
    MonthlyAccountBalanceService service;

    @Mock
    VoucherLineRepository voucherLineRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    AccountingPeriodService accountingPeriodService;
    @Mock
    MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;
    @Mock
    MonthlyBalanceCalculationService balanceCalculationService;

    private static final YearMonth MONTH = YearMonth.of(2026, 1);

    @Test
    @DisplayName("마감된 월이면 스냅샷으로 조회")
    void getMonthlyBalance_closedPeriod_snapshot() {
        // given
        Account account = AccountFixture.cash();
        MonthlyAccountBalanceCommand command = new MonthlyAccountBalanceCommand(account.getId(), MONTH);

        MonthlyAccountBalance snapshot = MonthlyAccountBalanceFixture.snapshot(1L, account, MONTH, "1500");

        given(accountingPeriodService.isPeriodClosed(MONTH)).willReturn(true);
        given(monthlyAccountBalanceRepository.findByPeriodAndAccountId(MONTH, account.getId())).willReturn(Optional.of(snapshot));

        // when
        MonthlyAccountBalanceResponse response = service.getMonthlyBalance(command);

        // then
        assertThat(response.getClosingBalance()).isEqualByComparingTo("1500");
        verify(monthlyAccountBalanceRepository).findByPeriodAndAccountId(MONTH, account.getId());
        verify(accountRepository, never()).findById(any());
        verify(balanceCalculationService, never()).calculateClosingBalance(any(), any(), any(), any());
    }

    @Test
    @DisplayName("미마감 월이면 실시간 계산")
    void getMonthlyBalance_openPeriod_realtime() {
        // given
        Account account = AccountFixture.cash();
        MonthlyAccountBalanceCommand command = new MonthlyAccountBalanceCommand(account.getId(), MONTH);

        given(accountingPeriodService.isPeriodClosed(MONTH)).willReturn(false);
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountingPeriodService.isPreviousPeriodClosed(MONTH)).willReturn(true);
        given(monthlyAccountBalanceRepository
                .findByPeriodAndAccountId(MONTH.minusMonths(1), account.getId()))
                .willReturn(Optional.empty());
        given(voucherLineRepository.findMonthlyTotal(any(), any(), any()))
                .willReturn(MonthlyBalanceQueryFixture.zero());
        given(balanceCalculationService.calculateClosingBalance(any(), any(), any(), any()))
                .willReturn(Money.of("1000"));

        // when
        MonthlyAccountBalanceResponse response = service.getMonthlyBalance(command);

        // then
        assertThat(response.getClosingBalance()).isEqualByComparingTo("1000");
        verify(accountRepository).findById(account.getId());
        verify(balanceCalculationService).calculateClosingBalance(any(), any(), any(), any());
    }
}
package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.balance.service.MonthlyBalanceCalculationService;
import com.erp.erp_accounting.accounting.ledger.repository.VoucherLineRepository;
import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriodStatus;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("회계기간 마감 서비스 테스트")
class AccountingPeriodCloseServiceTest {

    @InjectMocks
    private AccountingPeriodCloseService closeService;

    @Mock
    private AccountingPeriodService accountingPeriodService;
    @Mock
    private MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;
    @Mock
    private MonthlyBalanceCalculationService balanceCalculationService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private VoucherLineRepository voucherLineRepository;

    private static final YearMonth PERIOD = YearMonth.of(2026, 1);
    private User closer;

    @BeforeEach
    void setUp() {
        closer = UserFixture.adminUser();
    }

    @Test
    @DisplayName("회계기간 마감 성공")
    void closePeriod_success() {
        // given
        AccountingPeriod period = AccountingPeriod.open(PERIOD);

        given(accountingPeriodService.getPeriodForClosing(PERIOD)).willReturn(period);
        given(accountRepository.findLeafAccounts()).willReturn(List.of());
        given(monthlyAccountBalanceRepository.findByPeriod(any())).willReturn(List.of());
        given(voucherLineRepository.findMonthlyDebitSum(any(), any())).willReturn(List.of());
        given(voucherLineRepository.findMonthlyCreditSum(any(), any())).willReturn(List.of());
        given(balanceCalculationService.calculateMonthlyBalances(any(), any(), any(), any(), any())).willReturn(List.of());

        // when
        AccountingPeriodResponse response = closeService.closePeriod(PERIOD, closer);

        // then
        verify(accountingPeriodService).close(PERIOD, closer);
        verify(monthlyAccountBalanceRepository).deleteByPeriod(PERIOD);

        assertThat(response.getPeriod()).isEqualTo(PERIOD.toString());
    }

    @Test
    @DisplayName("대차 불일치 시 회계기간 마감 불가")
    void closeAccountingPeriod_fail_when_balance_unbalanced() {
        // given
        doThrow(new BusinessException(ErrorCode.IMBALANCE_AMOUNT, "대차 불일치"))
                .when(balanceCalculationService).validateMonthlyTrialBalance(any(), any(), eq(PERIOD));

        // when & then
        assertThatThrownBy(() -> closeService.closePeriod(PERIOD, closer))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.IMBALANCE_AMOUNT);
                    assertThat(be.getDetailMessage()).contains("대차 불일치");
                });
    }

    @Test
    @DisplayName("회계기간 마감 취소 성공")
    void reopenPeriod_success() {
        // given
        AccountingPeriod closed = AccountingPeriod.builder()
                .period(PERIOD)
                .status(AccountingPeriodStatus.CLOSED)
                .build();

        given(accountingPeriodService.getByPeriod(PERIOD)).willReturn(closed);

        // when
        AccountingPeriodResponse response = closeService.reopenPeriod(PERIOD, closer);

        // then
        verify(monthlyAccountBalanceRepository).deleteByPeriod(PERIOD);
        verify(accountingPeriodService).reopen(PERIOD, closer);
        assertThat(response.getPeriod()).isEqualTo(PERIOD.toString());
    }
}
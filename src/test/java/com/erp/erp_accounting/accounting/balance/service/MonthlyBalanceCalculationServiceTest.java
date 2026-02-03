package com.erp.erp_accounting.accounting.balance.service;

import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("월별 잔액 계산 서비스 테스트")
class MonthlyBalanceCalculationServiceTest {

    MonthlyBalanceCalculationService service = new MonthlyBalanceCalculationService();

    @Test
    @DisplayName("월별 잔액 계산 성공 - 차변 정상 잔액")
    void calculateClosingBalance_success() {
        // given
        NormalBalance normalBalance = NormalBalance.DEBIT;

        BigDecimal opening = Money.of("1000");
        BigDecimal debit = Money.of("500");
        BigDecimal credit = Money.of("200");

        // when
        BigDecimal closing = service.calculateClosingBalance(normalBalance, opening, debit, credit);

        // then
        assertThat(closing).isEqualByComparingTo("1300");
    }

    @Test
    @DisplayName("월별 대차 불일치 시 예외 발생")
    void validateMonthlyTrialBalance_fail() {
        // given
        Map<Long, BigDecimal> debitSums = Map.of(1L, Money.of("1000"));
        Map<Long, BigDecimal> creditSums = Map.of(1L, Money.of("900"));
        YearMonth period = YearMonth.of(2026, 1);

        // when & then
        assertThatThrownBy(() -> service.validateMonthlyTrialBalance(debitSums, creditSums, period))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode())
                            .isEqualTo(ErrorCode.IMBALANCE_AMOUNT);
                });
    }
}
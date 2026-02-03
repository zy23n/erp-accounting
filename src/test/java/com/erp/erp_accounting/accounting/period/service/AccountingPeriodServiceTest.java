package com.erp.erp_accounting.accounting.period.service;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.repository.AccountingPeriodRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("회계기간 서비스 테스트")
class AccountingPeriodServiceTest {

    @InjectMocks
    private AccountingPeriodService accountingPeriodService;

    @Mock
    private AccountingPeriodRepository accountingPeriodRepository;

    private static final YearMonth PERIOD = YearMonth.of(2026, 1);

    @Test
    @DisplayName("이전 회계기간 미마감 시 예외")
    void assertPreviousPeriodClosed_fail() {
        // given
        YearMonth prev = PERIOD.minusMonths(1);
        given(accountingPeriodRepository.findByPeriod(prev)).willReturn(Optional.of(AccountingPeriod.open(prev)));

        // when & then
        assertThatThrownBy(() -> accountingPeriodService.assertPreviousPeriodClosed(PERIOD))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PERIOD_NOT_CLOSED);
                });
    }
}
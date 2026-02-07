package com.erp.erp_accounting.integration.period;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.balance.repository.MonthlyAccountBalanceRepository;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.repository.AccountingPeriodRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodCloseService;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.AccountFixture;
import com.erp.erp_accounting.fixture.VoucherFixture;
import com.erp.erp_accounting.fixture.VoucherLineFixture;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("회계기간 마감 통합 테스트")
public class AccountingPeriodCloseIntegrationTest {

    @Autowired
    AccountingPeriodCloseService closeService;
    @Autowired
    AccountingPeriodService accountingPeriodService;
    @Autowired
    AccountingPeriodRepository accountingPeriodRepository;
    @Autowired
    MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;

    private User adminUser;
    private User accountingUser;
    private Account cash;
    private Account revenue;
    private final YearMonth PREV = YearMonth.of(2025, 12);
    private final YearMonth PERIOD = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        adminUser = userRepository.findByUsername("admin").orElseThrow();
        accountingUser = userRepository.findByUsername("accounting").orElseThrow();

        cash = accountRepository.findByCode("1010").orElseGet(() -> accountRepository.save(AccountFixture.cash()));
        revenue = accountRepository.findByCode("4000").orElseGet(() -> accountRepository.save(AccountFixture.revenue()));

        AccountingPeriod currentPeriod = accountingPeriodService.getOrCreate(PERIOD);
        AccountingPeriod prevPeriod = accountingPeriodService.getOrCreate(PREV);
        if (!prevPeriod.isClosed()) accountingPeriodService.close(PREV, adminUser);
    }

    @Test
    @DisplayName("회계기간 마감 성공")
    void close_period_success() {
        // given
        Voucher voucher = voucherRepository.save(VoucherFixture.draft(accountingUser));
        VoucherLineFixture.addBalancedLines(voucher, cash, revenue);
        voucher.approve(accountingUser);

        // when
        closeService.closePeriod(PERIOD, adminUser);

        // then
        AccountingPeriod period = accountingPeriodRepository.findByPeriod(PERIOD).orElseThrow();

        assertTrue(period.isClosed());
        assertEquals(adminUser.getId(), period.getClosedBy().getId());
        assertFalse(monthlyAccountBalanceRepository.findByPeriod(PERIOD).isEmpty());
    }

    @Test
    @DisplayName("이전 회계기간 미마감 시 실패")
    void fail_when_previous_period_not_closed() {
        // given
        closeService.reopenPeriod(PREV, adminUser);

        // when
        BusinessException ex = assertThrows(BusinessException.class, () -> closeService.closePeriod(PERIOD, adminUser));

        // then
        assertEquals(ErrorCode.PERIOD_NOT_CLOSED, ex.getErrorCode());
    }

    @Test
    @DisplayName("대차 불일치 시 마감 롤백")
    void rollback_when_trial_balance_imbalanced() {
        // given
        Voucher v1 = voucherRepository.save(VoucherFixture.draft(accountingUser));
        VoucherLineFixture.addBalancedLines(v1, cash, revenue);
        v1.approve(accountingUser);

        Voucher v2 = VoucherFixture.approvedWithoutValidation(accountingUser);
        VoucherLineFixture.addDebitOnly(v2, cash, BigDecimal.valueOf(500));
        voucherRepository.save(v2);

        // when
        assertThrows(BusinessException.class, () -> closeService.closePeriod(PERIOD, adminUser));

        // then
        AccountingPeriod period = accountingPeriodRepository.findByPeriod(PERIOD).orElseThrow();

        assertFalse(period.isClosed());
        assertNull(period.getClosedBy());
        assertTrue(monthlyAccountBalanceRepository.findByPeriod(PERIOD).isEmpty());
    }

    @Test
    @DisplayName("이미 마감된 회계기간 재마감 실패")
    void fail_when_already_closed() {
        // given
        closeService.closePeriod(PERIOD, adminUser);

        // when & then
        assertThrows(BusinessException.class, () -> closeService.closePeriod(PERIOD, adminUser));
    }

    @Test
    @DisplayName("마감 취소 성공")
    void reopen_period_success() {
        // given
        closeService.closePeriod(PERIOD, adminUser);

        // when
        closeService.reopenPeriod(PERIOD, adminUser);

        // then
        AccountingPeriod period = accountingPeriodRepository.findByPeriod(PERIOD).orElseThrow();

        assertFalse(period.isClosed());
        assertTrue(monthlyAccountBalanceRepository.findByPeriod(PERIOD).isEmpty());
    }
}

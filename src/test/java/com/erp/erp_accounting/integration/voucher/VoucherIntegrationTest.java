package com.erp.erp_accounting.integration.voucher;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.VoucherCommandFixture;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("전표 생성 통합 테스트")
class VoucherIntegrationTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountingPeriodService accountingPeriodService;

    private User normalUser;
    private Account cash;
    private Account revenue;
    private final YearMonth PERIOD = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        normalUser = userRepository.findByUsername("normal").orElseThrow();
        cash = accountRepository.findByCode("1010").orElseThrow();
        revenue = accountRepository.findByCode("4000").orElseThrow();
    }

    @Test
    @DisplayName("전표 생성 성공")
    void createVoucher_success() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(cash.getId(), revenue.getId());

        // when
        Voucher voucher = voucherService.createVoucher(command, normalUser);

        // then
        assertNotNull(voucher.getId());
        assertEquals(2, voucher.getLines().size());
        assertEquals(VoucherStatus.DRAFT, voucher.getStatus());
    }

    @Test
    @DisplayName("대차 불일치 전표 생성 시 예외")
    void createVoucher_imbalance_fail() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.imbalance(cash.getId(), revenue.getId());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () ->
                voucherService.createVoucher(command, normalUser));

        assertEquals(ErrorCode.IMBALANCE_AMOUNT, ex.getErrorCode());
        assertTrue(ex.hasDetail());
        assertEquals("요청 전표 대차 불일치 (차변 합계=1000, 대변 합계=500)", ex.getDetailMessage());
    }

    @Test
    @DisplayName("회계기간 마감 시 전표 생성 실패")
    void createVoucher_fail_when_period_closed() {
        // given
        accountingPeriodService.close(PERIOD, normalUser);
        CreateVoucherCommand command = VoucherCommandFixture.valid(cash.getId(), revenue.getId());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> voucherService.createVoucher(command, normalUser));

        assertEquals(ErrorCode.PERIOD_ALREADY_CLOSED, ex.getErrorCode());
        assertTrue(ex.getDetailMessage().contains("이미 마감된 회계기간"));
    }
}
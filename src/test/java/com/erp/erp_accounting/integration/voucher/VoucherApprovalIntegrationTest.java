package com.erp.erp_accounting.integration.voucher;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.service.VoucherApprovalService;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.UserFixture;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("전표 승인 통합 테스트")
class VoucherApprovalIntegrationTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherApprovalService voucherApprovalService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountingPeriodService accountingPeriodService;

    private User accountingUser;
    private Account cash;
    private Account revenue;
    private final YearMonth PERIOD = YearMonth.of(2026, 1);

    @BeforeEach
    void setUp() {
        accountingUser = userRepository.findByUsername("accounting").orElseThrow();
        cash = accountRepository.findByCode("1010").orElseThrow();
        revenue = accountRepository.findByCode("4000").orElseThrow();
    }

    @Test
    @DisplayName("전표 승인 성공")
    void approveVoucher_success() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(cash.getId(), revenue.getId());
        Voucher voucher = voucherService.createVoucher(command, accountingUser);

        // when
        VoucherApprovalResponse approved = voucherApprovalService.approve(voucher.getId(), accountingUser);

        // then
        assertThat(approved.getStatus()).isEqualTo(VoucherStatus.APPROVED);
        assertThat(approved.getProcessedById()).isEqualTo(accountingUser.getId());
        assertThat(approved.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("ACCOUNTING 권한 없으면 승인 실패")
    void approveVoucher_fail_when_no_permission() {
        // given
        User normalUser = userRepository.save(UserFixture.normalUser());

        CreateVoucherCommand command = VoucherCommandFixture.valid(cash.getId(), revenue.getId());
        Voucher voucher = voucherService.createVoucher(command, accountingUser);

        // when & then
        assertThatThrownBy(() -> voucherApprovalService.approve(voucher.getId(), normalUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("회계기간 마감 시 전표 승인 불가")
    void approveVoucher_fail_when_period_closed() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(cash.getId(), revenue.getId());
        Voucher voucher = voucherService.createVoucher(command, accountingUser);

        accountingPeriodService.close(PERIOD, accountingUser);

        // when & then
        assertThatThrownBy(() -> voucherApprovalService.approve(voucher.getId(), accountingUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PERIOD_ALREADY_CLOSED);
    }
}
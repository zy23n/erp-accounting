package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.AccountFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.fixture.VoucherFixture;
import com.erp.erp_accounting.fixture.VoucherLineFixture;
import com.erp.erp_accounting.user.entity.User;
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
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("전표 승인 서비스 테스트")
class VoucherApprovalServiceTest {

    @InjectMocks
    private VoucherApprovalService voucherApprovalService;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private AccountingPeriodService accountingPeriodService;

    @Mock
    private VoucherValidator validator;

    @Test
    @DisplayName("전표 승인 성공")
    void approve_success() {
        // given
        User accountingUser = UserFixture.accountingUser();
        Voucher voucher = VoucherFixture.savedDraft(1L, accountingUser);
        VoucherLineFixture.addBalancedLines(voucher, AccountFixture.cash(), AccountFixture.revenue());

        given(voucherRepository.findById(voucher.getId())).willReturn(Optional.of(voucher));

        // when
        VoucherApprovalResponse response = voucherApprovalService.approve(1L, accountingUser);

        // then
        assertThat(response.getStatus()).isEqualTo(VoucherStatus.APPROVED);
        assertThat(voucher.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("ACCOUNTING 권한 없으면 승인 실패")
    void approve_fail_no_permission() {
        // given
        User normalUser = UserFixture.normalUser();
        Voucher voucher = VoucherFixture.savedDraft(1L, normalUser);

        given(voucherRepository.findById(voucher.getId())).willReturn(Optional.of(voucher));

        // when & then
        assertThatThrownBy(() -> voucherApprovalService.approve(1L, normalUser))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("회계기간 마감 시 전표 승인 불가")
    void approveVoucher_fail_when_period_closed() {
        // given
        YearMonth period = YearMonth.of(2026, 1);
        User accountingUser = UserFixture.accountingUser();
        Voucher voucher = VoucherFixture.savedDraft(1L, accountingUser);

        given(voucherRepository.findById(voucher.getId())).willReturn(Optional.of(voucher));
        doThrow(new BusinessException(ErrorCode.PERIOD_ALREADY_CLOSED, "회계기간 마감"))
                .when(accountingPeriodService).assertPeriodOpen(period);

        // when & then
        assertThatThrownBy(() -> voucherApprovalService.approve(voucher.getId(), accountingUser))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PERIOD_ALREADY_CLOSED);
                    assertThat(be.getDetailMessage()).contains("회계기간 마감");
                });
    }

    @Test
    @DisplayName("전표 반려 성공")
    void reject_success() {
        // given
        User accountingUser = UserFixture.accountingUser();
        Voucher voucher = VoucherFixture.savedDraft(1L, accountingUser);

        given(voucherRepository.findById(voucher.getId())).willReturn(Optional.of(voucher));

        // when
        VoucherApprovalResponse response = voucherApprovalService.reject(1L, accountingUser);

        // then
        assertThat(response.getStatus()).isEqualTo(VoucherStatus.REJECTED);
        assertThat(voucher.getApprovedAt()).isNotNull();
    }
}
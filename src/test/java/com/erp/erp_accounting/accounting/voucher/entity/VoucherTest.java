package com.erp.erp_accounting.accounting.voucher.entity;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.fixture.VoucherFixture;
import com.erp.erp_accounting.fixture.VoucherLineFixture;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("전표 엔티티 테스트")
class VoucherTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.normalUser();
    }

    @Test
    @DisplayName("대차 일치 시 전표 승인 성공")
    void approve_success_when_balanced() {
        // given
        Voucher voucher = VoucherFixture.savedDraft(1L, user);
        VoucherLineFixture.addBalancedLines(voucher);

        // when
        voucher.approve(user);

        // then
        assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.APPROVED);
        assertThat(voucher.getApprovedBy()).isEqualTo(user);
        assertThat(voucher.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("대차 불일치 시 전표 승인 실패")
    void approve_fail_when_unbalanced() {
        // given
        Voucher voucher = VoucherFixture.savedDraft(1L, user);
        VoucherLineFixture.addUnbalancedLines(voucher);

        // when & then
        assertThatThrownBy(() -> voucher.approve(user))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.IMBALANCE_AMOUNT);
                });
    }
}
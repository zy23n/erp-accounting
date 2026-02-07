package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.AccountFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.fixture.VoucherCommandFixture;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("전표 서비스 테스트")
class VoucherServiceTest {

    @InjectMocks
    private VoucherService voucherService;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountingPeriodService accountingPeriodService;

    @Mock
    private VoucherValidator validator;

    private User user;
    private Long debitAccountId;
    private Long creditAccountId;

    @BeforeEach
    void setUp() {
        user = UserFixture.normalUser();
        debitAccountId = 1L;
        creditAccountId = 2L;
    }

    @Test
    @DisplayName("전표 생성 성공")
    void createVoucher_success() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(debitAccountId, creditAccountId);

        given(accountRepository.findById(debitAccountId)).willReturn(Optional.of(AccountFixture.cash()));
        given(accountRepository.findById(creditAccountId)).willReturn(Optional.of(AccountFixture.revenue()));

        // when
        Voucher voucher = voucherService.createVoucher(command, user);

        // then
        assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.DRAFT);
        assertThat(voucher.getLines()).hasSize(2);
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    @DisplayName("대차 불일치 시 전표 생성 실패")
    void createVoucher_fail_when_debit_credit_unbalanced() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(debitAccountId, creditAccountId);

        willThrow(new BusinessException(ErrorCode.IMBALANCE_AMOUNT, "요청 전표 대차 불일치 (차변 합계=1000, 대변 합계=900)"))
                .given(validator).validateForCreate(any());

        // when & then
        assertThatThrownBy(() -> voucherService.createVoucher(command, user))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.IMBALANCE_AMOUNT);
                });
        verify(validator).validateForCreate(any());
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("회계기간 마감 시 전표 생성 불가")
    void createVoucher_fail_when_period_closed() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(debitAccountId, creditAccountId);

        willThrow(new BusinessException(ErrorCode.PERIOD_ALREADY_CLOSED, "회계기간 마감"))
                .given(accountingPeriodService).assertPeriodOpen(any());

        // when & then
        assertThatThrownBy(() -> voucherService.createVoucher(command, user))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PERIOD_ALREADY_CLOSED);
                    assertThat(be.getDetailMessage()).contains("회계기간 마감");
                });
    }

    @Test
    @DisplayName("validator가 예외를 던지면 그대로 전파")
    void createVoucher_fail_when_validator_throws() {
        // given
        CreateVoucherCommand command = VoucherCommandFixture.valid(debitAccountId, creditAccountId);

        willThrow(new BusinessException(ErrorCode.INVALID_REQUEST, "전표 라인 오류"))
                .given(validator).validateForCreate(any());

        // when & then
        assertThatThrownBy(() -> voucherService.createVoucher(command, user))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }
}
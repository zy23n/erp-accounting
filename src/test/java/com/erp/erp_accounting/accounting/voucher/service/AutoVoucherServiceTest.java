package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.service.AccountService;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollConfirmFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("자동 분개 서비스 테스트")
class AutoVoucherServiceTest {

    @InjectMocks
    private AutoVoucherService autoVoucherService;

    @Mock
    private VoucherRepository voucherRepository;
    @Mock
    private VoucherService voucherService;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountingPeriodService accountingPeriodService;

    private PayrollConfirm confirm;
    private User confirmer;

    @BeforeEach
    void setUp() {
        confirmer = UserFixture.accountingUser();
        ReflectionTestUtils.setField(confirmer, "id", 20L);

        Employee employee = EmployeeFixture.employee(1L);
        Payroll payroll = PayrollFixture.calculatedPayroll(employee);

        confirm = PayrollConfirmFixture.saved(1L, YearMonth.of(2026, 1));
        confirm.addPayroll(payroll);
        confirm.confirm(confirmer);
    }

    @Test
    @DisplayName("급여 확정 기반 자동분개 생성 성공")
    void createFromPayrollConfirm_success() {
        // given
        given(voucherRepository.existsBySourceTypeAndSourceIdAndStatusNot(
                SourceType.PAYROLL, confirm.getId(), VoucherStatus.CANCELED))
                .willReturn(false);

        given(accountService.getAccountIdByPayrollItem(any())).willReturn(100L);
        given(accountService.getAccountIdByPaymentMethod(any())).willReturn(200L);

        Voucher voucher = mock(Voucher.class);
        given(voucherService.createVoucher(any(), eq(confirmer))).willReturn(voucher);
        given(voucher.getCreatedBy()).willReturn(confirmer);

        // when
        autoVoucherService.createFromPayrollConfirm(confirm);

        // then
        verify(voucherService).createVoucher(any(CreateVoucherCommand.class), eq(confirmer));
        verify(voucher).approve(confirmer);
    }

    @Test
    @DisplayName("급여 확정 취소 시 자동분개 취소")
    void cancelAutoVouchers_success() {
        // given
        Voucher voucher = mock(Voucher.class);
        given(voucherRepository.findBySourceTypeAndSourceId(SourceType.PAYROLL, confirm.getId()))
                .willReturn(List.of(voucher));

        given(voucher.getVoucherDate()).willReturn(LocalDate.of(2026, 1, 31));
        given(voucher.isCancelable()).willReturn(true);

        // when
        autoVoucherService.cancelAutoVouchersBySource(SourceType.PAYROLL, confirm.getId(), confirmer);

        // then
        verify(voucher).cancel(confirmer);
    }
}
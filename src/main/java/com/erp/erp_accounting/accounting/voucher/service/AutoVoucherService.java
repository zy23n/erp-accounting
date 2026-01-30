package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.service.AccountService;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollItem;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutoVoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;
    private final AccountService accountService;
    private final AccountingPeriodService accountingPeriodService;

    public void createFromPayrollConfirm(PayrollConfirm confirm) {
        // 중복 분개 방지
        boolean existsActiveVoucher =
                voucherRepository.existsBySourceTypeAndSourceIdAndStatusNot(
                    SourceType.PAYROLL, confirm.getId(), VoucherStatus.CANCELED);
        if (existsActiveVoucher) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "급여 확정 전표 중복 생성");
        }

        // 전표 라인 생성
        List<VoucherLineRequest> lines = buildVoucherLines(confirm);

        // 전표 생성 DTO
        LocalDate voucherDate = confirm.getPayMonth().atEndOfMonth();
        CreateVoucherCommand command = new CreateVoucherCommand(
                voucherDate,
                "급여 자동분개: " + confirm.getPayMonth(),
                lines,
                SourceType.PAYROLL,
                confirm.getId()
        );

        log.info("[AUTO-VOUCHER] action=CREATE_REQUEST, sourceType={}, sourceId={}, userId={}",
                SourceType.PAYROLL, confirm.getId(), confirm.getConfirmedBy().getId());

        // 전표 생성 + 승인
        Voucher voucher = voucherService.createVoucher(command, confirm.getConfirmedBy());
        voucher.approve(voucher.getCreatedBy());

        log.info("[AUTO-VOUCHER] action=CREATE_COMPLETE, voucherId={}, voucherNo={}, sourceType={}, sourceId={}, userId={}",
                voucher.getId(), voucher.getVoucherNo(), voucher.getSourceType(), voucher.getSourceId(), voucher.getCreatedBy().getId());
    }

    public void cancelAutoVouchersBySource(SourceType sourceType, Long sourceId, User canceler) {

        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(sourceType, sourceId);
        if (vouchers.isEmpty()) return;

        log.info("[AUTO-VOUCHER] action=AUTO_CANCEL_REQUEST, sourceType={}, sourceId={}, cancelerId={}",
                sourceType, sourceId, canceler.getId());

        assertVoucherPeriodOpen(vouchers.get(0).getVoucherDate());

        for (Voucher voucher : vouchers) {
            if (voucher.isCancelable()) {
                voucher.cancel(canceler);
            }
        }

        log.warn("[AUTO-VOUCHER] action=AUTO_CANCEL_COMPLETE, sourceType={}, sourceId={}, cancelerId={}",
                sourceType, sourceId, canceler.getId());
    }

        private List<VoucherLineRequest> buildVoucherLines(PayrollConfirm confirm) {

            List<VoucherLineRequest> lines = new ArrayList<>();

            // 계정 매핑
            Long salaryAccount = accountService.getAccountIdByPayrollItem(PayrollItem.BASE_SALARY);
            Long allowanceAccount = accountService.getAccountIdByPayrollItem(PayrollItem.BONUS);
            Long deductionAccount = accountService.getAccountIdByPayrollItem(PayrollItem.DEDUCTION);

            // 급여 항목별 전표 라인 생성
            for (Payroll payroll : confirm.getPayrolls()) {

                addDebit(lines, salaryAccount, payroll.getBaseSalary());
                addDebit(lines, allowanceAccount, payroll.getAllowanceAmount());
                addCredit(lines, deductionAccount, payroll.getDeductionAmount());

                if (payroll.getNetAmount() == null) payroll.calculateNetAmount();

                Long paymentAccount = accountService.getAccountIdByPaymentMethod(payroll.getPaymentMethod());
                addCredit(lines, paymentAccount, payroll.getNetAmount());
            }

            return lines;
        }

    private void addDebit(List<VoucherLineRequest> lines, Long accountId, BigDecimal amount) {
        if (amount.signum() > 0) {
            lines.add(new VoucherLineRequest(accountId, LineType.DEBIT, amount));
        }
    }

    private void addCredit(List<VoucherLineRequest> lines, Long accountId, BigDecimal amount) {
        if (amount.signum() > 0) {
            lines.add(new VoucherLineRequest(accountId, LineType.CREDIT, amount));
        }
    }
    private void assertVoucherPeriodOpen(LocalDate voucherDate) {
        accountingPeriodService.assertPeriodOpen(YearMonth.from(voucherDate));
    }
}

package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.service.AccountService;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AutoVoucherService {

    private  final VoucherRepository voucherRepository;
    private final VoucherService voucherService;
    private final AccountService accountService;

    public void createFromPayrollConfirm(PayrollConfirm confirm) {
        // 중복 분개 방지
        boolean existsActiveVoucher =
                voucherRepository.existsBySourceTypeAndSourceIdAndStatusNot(
                    SourceType.PAYROLL, confirm.getId(), VoucherStatus.CANCELED);
        if (existsActiveVoucher) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "급여 확정 전표 중복 생성");
        }

        List<VoucherLineRequest> lines = new ArrayList<>();

        // 계정 매핑
        Long salaryAccount = accountService.getAccountId(PayrollItem.BASE_SALARY);
        Long allowanceAccount = accountService.getAccountId(PayrollItem.BONUS);
        Long deductionAccount = accountService.getAccountId(PayrollItem.DEDUCTION);
        Long cashAccount = accountService.getAccountId(PayrollItem.CASH);

        // 급여 항목별 전표 라인 생성
        for (Payroll payroll : confirm.getPayrolls()) {

            addDebit(lines, salaryAccount, payroll.getBaseSalary());
            addDebit(lines, allowanceAccount, payroll.getAllowanceAmount());
            addCredit(lines, deductionAccount, payroll.getDeductionAmount());

            if (payroll.getNetAmount() == null) payroll.calculateNetAmount();
            addCredit(lines, cashAccount, payroll.getNetAmount());
        }

        // 전표 생성 DTO
        LocalDate voucherDate = confirm.getPayMonth().atEndOfMonth();
        CreateVoucherCommand command = new CreateVoucherCommand(
                voucherDate,
                "급여 자동분개: " + confirm.getPayMonth(),
                lines,
                SourceType.PAYROLL,
                confirm.getId()
        );

        // 전표 생성 + 자동 승인
        voucherService.createAndAutoApprove(command, confirm.getConfirmedBy());
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
}

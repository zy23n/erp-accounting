package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.common.BalanceSum;
import com.erp.erp_accounting.accounting.common.DebitCreditCalculator;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.LineAmount;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class VoucherValidator {

    // 생성
    public void validateForCreate(List<VoucherLineRequest> lines) {
        validateLines(lines);
    }

    // 승인
    public void validateForApprove(Voucher voucher) {
        assertDraft(voucher);
        validateDebitCreditBalance(voucher.getLines());
    }

    /* ===== 공통 메서드 ===== */
    private void validateLines(List<VoucherLineRequest> lines) {
        validateLineExistence(lines);
        validateLineFields(lines);
        validateDebitCreditPresence(lines);
        validateDebitCreditBalance(lines);
    }

    private void validateLineExistence(List<?> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "전표 라인 미존재");
        }
    }

    private void validateLineFields(List<VoucherLineRequest> lines) {
        for (VoucherLineRequest line : lines) {
            if (line.getType() == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "차변/대변 타입 미입력");
            }
            if (line.getAmount() == null || line.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "전표 라인 금액 오류");
            }
        }
    }

    private void validateDebitCreditPresence(List<VoucherLineRequest> lines) {
        boolean hasDebit = false;
        boolean hasCredit = false;

        for (VoucherLineRequest line : lines) {
            if (line.getType() == LineType.DEBIT) hasDebit = true;
            if (line.getType() == LineType.CREDIT) hasCredit = true;
        }

        if (!hasDebit || !hasCredit) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "차변 또는 대변 라인 부족");
        }
    }

    private void validateDebitCreditBalance(List<? extends LineAmount> lines) {
        BalanceSum sum = DebitCreditCalculator.calculate(lines);
        if (!sum.isBalanced()) {
            throw new BusinessException(ErrorCode.IMBALANCE_AMOUNT,
                    String.format("요청 전표 대차 불일치 (차변 합계=%s, 대변 합계=%s)", sum.getDebit(), sum.getCredit()));
        }
    }

    private void assertDraft(Voucher voucher) {
        if (voucher.getStatus() != VoucherStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "전표 상태 오류 (required=DRAFT)");
        }
    }
}

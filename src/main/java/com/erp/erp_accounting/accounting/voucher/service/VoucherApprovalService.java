package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VoucherApprovalService {

    private final VoucherRepository voucherRepository;
    private final AccountingPeriodService accountingPeriodService;
    private final VoucherValidator validator;

    public VoucherApprovalResponse approve(Long voucherId, User approver) {

        Voucher voucher = findVoucher(voucherId);

        if (!approver.getRoles().contains(UserRole.ACCOUNTING)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "전표 승인 권한 없음");
        }

        assertPeriodOpen(voucher);

        validator.validateForApprove(voucher);
        voucher.approve(approver);

        log.info("[VOUCHER_APPROVED] voucherId={}, approverId={}", voucherId, approver.getId());

        return toResponse(voucher);
    }

    public VoucherApprovalResponse reject(Long voucherId, User approver) {

        Voucher voucher = findVoucher(voucherId);

        if (!approver.getRoles().contains(UserRole.ACCOUNTING)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "전표 반려 권한 없음");
        }

        assertPeriodOpen(voucher);

        voucher.reject(approver);

        log.info("[VOUCHER_REJECTED] voucherId={}, approverId={}", voucherId, approver.getId());

        return toResponse(voucher);
    }

    private Voucher findVoucher(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("전표 미존재 (voucherId=%d)", voucherId)));
    }

    private void assertPeriodOpen(Voucher voucher) {
        accountingPeriodService.assertPeriodOpen(YearMonth.from(voucher.getVoucherDate()));
    }

    private VoucherApprovalResponse toResponse(Voucher voucher) {
        return new VoucherApprovalResponse(
                voucher.getId(),
                voucher.getStatus().name(),
                voucher.getApprovedBy().getId(),
                voucher.getApprovedBy().getUsername(),
                voucher.getApprovedAt(),
                voucher.getVoucherType(),
                voucher.getSourceType(),
                voucher.getSourceId()
        );
    }
}

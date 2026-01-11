package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherApprovalService {

    private final VoucherRepository voucherRepository;
    private final AccountingPeriodService accountingPeriodService;

    public VoucherApprovalResponse approve(Long voucherId, User approver) {

        Voucher voucher = findVoucher(voucherId);

        if (!approver.getRoles().contains(UserRole.ACCOUNTING)) {
            throw new AccessDeniedException("회계 담당자만 전표 승인 가능");
        }

        assertPeriodOpen(voucher);

        voucher.approve(approver);

        return toResponse(voucher);
    }

    public VoucherApprovalResponse reject(Long voucherId, User approver) {

        Voucher voucher = findVoucher(voucherId);

        if (!approver.getRoles().contains(UserRole.ACCOUNTING)) {
            throw new AccessDeniedException("회계 담당자만 전표 반려 가능");
        }

        assertPeriodOpen(voucher);

        voucher.reject(approver);

        return toResponse(voucher);
    }

    private Voucher findVoucher(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("전표 없음"));
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

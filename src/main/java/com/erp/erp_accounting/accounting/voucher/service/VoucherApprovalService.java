package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherApprovalService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final AccountingPeriodService accountingPeriodService;

    public VoucherApprovalResponse approve(Long voucherId, Long userId) {
        Voucher voucher = findVoucher(voucherId);
        assertPeriodOpen(voucher);
        User user = findUser(userId);

        voucher.approve(user);

        return toResponse(voucher);
    }

    public VoucherApprovalResponse reject(Long voucherId, Long userId) {
        Voucher voucher = findVoucher(voucherId);
        assertPeriodOpen(voucher);
        User user = findUser(userId);

        voucher.reject(user);

        return toResponse(voucher);
    }

    private Voucher findVoucher(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("전표 없음"));
    }

    private void assertPeriodOpen(Voucher voucher) {
        accountingPeriodService.assertPeriodOpen(YearMonth.from(voucher.getVoucherDate()));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
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

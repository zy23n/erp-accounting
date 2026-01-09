package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.service.VoucherApprovalService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherApprovalController {

    private final VoucherApprovalService approvalService;

    // 승인
    @PatchMapping("/{voucherId}/approve")
    public ResponseEntity<VoucherApprovalResponse> approve(
            @PathVariable("voucherId") Long voucherId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(approvalService.approve(voucherId, principal.getUser()));
    }

    // 반려
    @PatchMapping("/{voucherId}/reject")
    public ResponseEntity<VoucherApprovalResponse> reject(
            @PathVariable("voucherId") Long voucherId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(approvalService.reject(voucherId, principal.getUser()));
    }
}

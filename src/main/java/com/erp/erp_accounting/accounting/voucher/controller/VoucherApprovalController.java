package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherApprovalResponse;
import com.erp.erp_accounting.accounting.voucher.service.VoucherApprovalService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "전표 승인", description = "전표 승인 및 반려 API")
public class VoucherApprovalController {

    private final VoucherApprovalService approvalService;

    @Operation(summary = "전표 승인", description = "지정한 전표를 승인 처리합니다.")
    @PatchMapping("/{voucherId}/approve")
    @PreAuthorize("hasRole('ACCOUNTING')")
    public ResponseEntity<VoucherApprovalResponse> approve(
            @Parameter(description = "전표 ID", required = true, example = "100")
            @PathVariable("voucherId") Long voucherId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(approvalService.approve(voucherId, principal.getUser()));
    }

    @Operation(summary = "전표 반려", description = "지정한 전표를 반려 처리합니다.")
    @PatchMapping("/{voucherId}/reject")
    @PreAuthorize("hasRole('ACCOUNTING')")
    public ResponseEntity<VoucherApprovalResponse> reject(
            @Parameter(description = "전표 ID", required = true, example = "100")
            @PathVariable("voucherId") Long voucherId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(approvalService.reject(voucherId, principal.getUser()));
    }
}

package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<Long> createVoucher(
            @RequestBody VoucherCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Voucher voucher = voucherService.createVoucher(request, principal.getUser());
        return ResponseEntity.ok(voucher.getId());
    }
}

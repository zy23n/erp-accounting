package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import jakarta.validation.Valid;
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
            @RequestBody @Valid VoucherCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CreateVoucherCommand command = new CreateVoucherCommand(
                request.getVoucherDate(),
                request.getDescription(),
                request.getLines(),
                SourceType.NONE,
                null
        );

        Voucher voucher = voucherService.createVoucher(command, principal.getUser());
        return ResponseEntity.ok(voucher.getId());
    }
}

package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.service.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherQueryController {

    private final VoucherQueryService voucherQueryService;

    // 단건 조회
    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> getVoucher(
            @PathVariable("voucherId") Long voucherId
    ) {
        return ResponseEntity.ok(
                voucherQueryService.getVoucher(voucherId)
        );
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<VoucherListResponse>> getVoucherList() {
        return ResponseEntity.ok(voucherQueryService.getVoucherList());
    }
}

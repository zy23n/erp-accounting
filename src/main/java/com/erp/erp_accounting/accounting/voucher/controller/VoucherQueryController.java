package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.service.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherQueryController {

    private final VoucherQueryService voucherQueryService;

    // 단건 조회
    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> getVoucher(@PathVariable("voucherId") Long voucherId) {
        return ResponseEntity.ok(voucherQueryService.getVoucher(voucherId));
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<Page<VoucherListResponse>> getVoucherList(Pageable pageable) {
        Pageable safePageable = voucherQueryService.validateSortFields(pageable);
        return ResponseEntity.ok(voucherQueryService.getVoucherList(safePageable));
    }
}

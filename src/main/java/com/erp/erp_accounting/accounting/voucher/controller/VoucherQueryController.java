package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.query.VoucherSearchCondition;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.service.VoucherQueryService;
import com.erp.erp_accounting.security.annotation.CurrentUser;
import com.erp.erp_accounting.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "전표 조회", description = "전표 단건 및 목록 조회 API")
public class VoucherQueryController {

    private final VoucherQueryService voucherQueryService;

    @Operation(summary = "전표 단건 조회", description = "전표 ID를 기준으로 전표 상세 정보를 조회합니다.")
    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> getVoucher(
            @Parameter(description = "전표 ID", required = true, example = "1")
            @PathVariable("voucherId") Long voucherId,
            @CurrentUser User user
    ) {
        return ResponseEntity.ok(voucherQueryService.getVoucher(voucherId, user));
    }

    @Operation(summary = "전표 목록 조회", description = "검색 조건과 페이징 정보를 기반으로 전표 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<VoucherListResponse>> getVoucherList(
            @CurrentUser User user,
            @Parameter(description = "전표 검색 조건") @ModelAttribute VoucherSearchCondition condition,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(voucherQueryService.searchVouchers(user, condition, pageable));
    }
}

package com.erp.erp_accounting.accounting.voucher.controller;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "전표", description = "전표 생성 및 관리 API")
public class VoucherController {

    private final VoucherService voucherService;

    @Operation(summary = "전표 생성", description = "입력한 전표 정보와 전표 라인을 기반으로 새로운 전표를 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createVoucher(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "전표 생성 요청 정보", required = true)
            @RequestBody @Valid VoucherCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal
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

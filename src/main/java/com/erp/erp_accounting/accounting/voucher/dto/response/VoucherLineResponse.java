package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.VoucherLine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "전표 라인 응답 DTO")
public class VoucherLineResponse {

    @Schema(description = "전표 라인 ID", example = "1")
    private Long id;

    @Schema(description = "계정 ID", example = "10")
    private Long accountId;

    @Schema(description = "계정명", example = "급여")
    private String accountName;

    @Schema(description = "차변/대변 구분", example = "DEBIT")
    private String type;

    @Schema(description = "금액", example = "1500000")
    private BigDecimal amount;

    public static VoucherLineResponse fromEntity(VoucherLine line) {
        return VoucherLineResponse.builder()
                .id(line.getId())
                .accountId(line.getAccount().getId())
                .accountName(line.getAccount().getName())
                .type(line.getType().name())
                .amount(line.getAmount())
                .build();
    }

}
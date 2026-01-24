package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "전표 승인/반려 결과 응답 DTO")
public class VoucherApprovalResponse {

    @Schema(description = "전표 ID", example = "1")
    private Long voucherId;

    @Schema(description = "전표 상태", example = "APPROVED")
    private VoucherStatus status;

    @Schema(description = "처리 사용자 ID", example = "1")
    private Long processedById;

    @Schema(description = "처리 사용자 계정명", example = "accounting_admin")
    private String processedByUsername;

    @Schema(description = "처리 일시", example = "2026-01-15T14:30:00")
    private LocalDateTime processedAt;

    @Schema(description = "전표 유형", example = "GENERAL")
    private VoucherType voucherType;

    @Schema(description = "출처 유형", example = "NONE")
    private SourceType sourceType;

    @Schema(description = "출처 ID", example = "1")
    private Long sourceId;

    public static VoucherApprovalResponse fromEntity(Voucher voucher) {
        return VoucherApprovalResponse.builder()
                .voucherId(voucher.getId())
                .status(voucher.getStatus())
                .processedById(getUserId(voucher.getApprovedBy()))
                .processedByUsername(getUsername(voucher.getApprovedBy()))
                .processedAt(voucher.getApprovedAt())
                .voucherType(voucher.getVoucherType())
                .sourceType(voucher.getSourceType())
                .sourceId(voucher.getSourceId())
                .build();
    }
}

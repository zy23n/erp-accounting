package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "전표 상세 응답 DTO")
public class VoucherResponse {

    @Schema(description = "전표 ID", example = "1")
    private Long voucherId;

    @Schema(description = "전표 번호", example = "V1769281057499")
    private String voucherNo;

    @Schema(description = "전표 일자", example = "2026-01-15")
    private LocalDate voucherDate;

    @Schema(description = "적요", example = "1월 급여 지급")
    private String description;

    @Schema(description = "전표 상태", example = "APPROVED")
    private VoucherStatus status;

    @Schema(description = "작성자 ID", example = "10")
    private Long createdById;

    @Schema(description = "작성자 계정명", example = "user1")
    private String createdByUsername;

    @Schema(description = "전표 라인 목록")
    private List<VoucherLineResponse> lines;

    @Schema(description = "전표 유형", example = "PAYROLL")
    private VoucherType voucherType;

    @Schema(description = "출처 유형", example = "PAYROLL")
    private SourceType sourceType;

    @Schema(description = "출처 ID", example = "1")
    private Long sourceId;

    @Schema(description = "처리자 ID", example = "1")
    private Long processedById;

    @Schema(description = "처리자 계정명", example = "accounting_admin")
    private String processedByUsername;

    @Schema(description = "처리 일시")
    private LocalDateTime processedAt;

    @Schema(description = "취소자 ID", example = "1")
    private Long canceledBy;

    @Schema(description = "취소자 계정명", example = "accounting_admin")
    private String canceledByUsername;

    @Schema(description = "취소 일시")
    private LocalDateTime canceledAt;

    public static VoucherResponse fromEntity(Voucher voucher) {
        List<VoucherLineResponse> lines = voucher.getLines().stream()
                .map(VoucherLineResponse::fromEntity)
                .toList();

        return VoucherResponse.builder()
                .voucherId(voucher.getId())
                .voucherNo(voucher.getVoucherNo())
                .voucherDate(voucher.getVoucherDate())
                .description(voucher.getDescription())
                .status(voucher.getStatus())
                .createdById(getUserId(voucher.getCreatedBy()))
                .createdByUsername(getUsername(voucher.getCreatedBy()))
                .lines(lines)
                .voucherType(voucher.getVoucherType())
                .sourceType(voucher.getSourceType())
                .sourceId(voucher.getSourceId())
                .processedById(getUserId(voucher.getApprovedBy()))
                .processedByUsername(getUsername(voucher.getApprovedBy()))
                .processedAt(voucher.getApprovedAt())
                .canceledBy(getUserId(voucher.getCanceledBy()))
                .canceledByUsername(getUsername(voucher.getCanceledBy()))
                .canceledAt(voucher.getCanceledAt())
                .build();
    }
}

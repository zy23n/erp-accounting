package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
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
public class VoucherResponse {
    private Long voucherId;
    private String voucherNo;
    private LocalDate voucherDate;
    private String description;
    private VoucherStatus status; // DRAFT, APPROVED, REJECTED, CANCELED
    private Long createdById;
    private String createdByUsername;
    private List<VoucherLineResponse> lines;

    private VoucherType voucherType;
    private SourceType sourceType;
    private Long sourceId;

    private Long approvedById;
    private String approvedByUsername;
    private LocalDateTime approvedAt;

    private Long canceledBy;
    private String canceledByUsername;
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
                .approvedById(getUserId(voucher.getApprovedBy()))
                .approvedByUsername(getUsername(voucher.getApprovedBy()))
                .approvedAt(voucher.getApprovedAt())
                .canceledBy(getUserId(voucher.getCanceledBy()))
                .canceledByUsername(getUsername(voucher.getCanceledBy()))
                .canceledAt(voucher.getCanceledAt())
                .build();
    }
}

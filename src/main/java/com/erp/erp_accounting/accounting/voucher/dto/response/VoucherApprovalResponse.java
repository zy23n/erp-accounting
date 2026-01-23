package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import lombok.*;

import java.time.LocalDateTime;

import static com.erp.erp_accounting.common.util.DtoUtils.getUserId;
import static com.erp.erp_accounting.common.util.DtoUtils.getUsername;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class VoucherApprovalResponse {
    private Long voucherId;
    private String status;
    private Long approvedById;
    private String approvedByUsername;
    private LocalDateTime approvedAt;

    private VoucherType voucherType;
    private SourceType sourceType;
    private Long sourceId;

    public static VoucherApprovalResponse fromEntity(Voucher voucher) {
        return VoucherApprovalResponse.builder()
                .voucherId(voucher.getId())
                .status(voucher.getStatus().name())
                .approvedById(getUserId(voucher.getApprovedBy()))
                .approvedByUsername(getUsername(voucher.getApprovedBy()))
                .approvedAt(voucher.getApprovedAt())
                .voucherType(voucher.getVoucherType())
                .sourceType(voucher.getSourceType())
                .sourceId(voucher.getSourceId())
                .build();
    }
}

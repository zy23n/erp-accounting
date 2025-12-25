package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
}

package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private Long voucherId;
    private String voucherNo;
    private LocalDate voucherDate;
    private String description;
    private VoucherStatus status; // DRAFT, APPROVED, REJECTED
    private Long createdBy;
    private List<VoucherLineResponse> lines;

    private VoucherType voucherType;
    private SourceType sourceType;
    private Long sourceId;
}

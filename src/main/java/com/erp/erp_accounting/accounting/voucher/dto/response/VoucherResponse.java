package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class VoucherResponse {
    private Long voucherId;
    private String voucherNo;
    private LocalDate voucherDate;
    private String description;
    private VoucherStatus status; // DRAFT, APPROVED, REJECTED
    private Long createdBy;
    private List<VoucherLineResponse> lines;
}

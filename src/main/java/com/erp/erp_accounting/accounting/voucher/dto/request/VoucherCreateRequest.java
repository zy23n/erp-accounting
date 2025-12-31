package com.erp.erp_accounting.accounting.voucher.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCreateRequest {
    private LocalDate voucherDate;
    private String description;
    private List<VoucherLineRequest> lines;

    private Long sourceId;
}

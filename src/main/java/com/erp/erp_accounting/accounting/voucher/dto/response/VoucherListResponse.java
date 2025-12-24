package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VoucherListResponse {
    private Long id;
    private String voucherNo;
    private LocalDate voucherDate;
    private VoucherStatus status;
    private String description;

    private Long createdById;
    private String createdByUsername;

    private BigDecimal debitSum;
    private BigDecimal creditSum;

    private LocalDateTime createdAt;
}

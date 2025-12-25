package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
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

    private VoucherType voucherType;
    private SourceType sourceType;
}

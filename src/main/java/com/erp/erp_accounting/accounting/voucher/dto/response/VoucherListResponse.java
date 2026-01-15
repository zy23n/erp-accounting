package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
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

    @QueryProjection
    public VoucherListResponse(
            Long id,
            String voucherNo,
            LocalDate voucherDate,
            VoucherStatus status,
            String description,
            Long createdById,
            String createdByUsername,
            BigDecimal debitSum,
            BigDecimal creditSum,
            LocalDateTime createdAt,
            VoucherType voucherType,
            SourceType sourceType
    ) {
        this.id = id;
        this.voucherNo = voucherNo;
        this.voucherDate = voucherDate;
        this.status = status;
        this.description = description;
        this.createdById = createdById;
        this.createdByUsername = createdByUsername;
        this.debitSum = debitSum;
        this.creditSum = creditSum;
        this.createdAt = createdAt;
        this.voucherType = voucherType;
        this.sourceType = sourceType;
    }
}

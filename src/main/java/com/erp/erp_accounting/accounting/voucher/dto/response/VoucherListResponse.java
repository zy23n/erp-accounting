package com.erp.erp_accounting.accounting.voucher.dto.response;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@Schema(description = "전표 목록 응답 DTO")
public class VoucherListResponse {

    @Schema(description = "전표 ID", example = "1")
    private Long id;

    @Schema(description = "전표 번호", example = "V1769281057499")
    private String voucherNo;

    @Schema(description = "전표 일자", example = "2026-01-15")
    private LocalDate voucherDate;

    @Schema(description = "전표 상태", example = "APPROVED")
    private VoucherStatus status;

    @Schema(description = "적요", example = "사무용품 외상 결제")
    private String description;

    @Schema(description = "작성자 ID", example = "10")
    private Long createdById;

    @Schema(description = "작성자 계정명", example = "user1")
    private String createdByUsername;

    @Schema(description = "차변 합계", example = "15000")
    private BigDecimal totalDebit;

    @Schema(description = "대변 합계", example = "15000")
    private BigDecimal totalCredit;

    @Schema(description = "작성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "전표 유형", example = "GENERAL")
    private VoucherType voucherType;

    @Schema(description = "출처 유형", example = "NONE")
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
            BigDecimal totalDebit,
            BigDecimal totalCredit,
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
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.createdAt = createdAt;
        this.voucherType = voucherType;
        this.sourceType = sourceType;
    }
}

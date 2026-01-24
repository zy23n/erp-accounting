package com.erp.erp_accounting.accounting.voucher.dto.query;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "전표 검색 조건 DTO")
public class VoucherSearchCondition {

    @Schema(description = "전표 번호", example = "V1769281057499")
    private String voucherNo;

    @Schema(description = "전표 상태", example = "APPROVED")
    private VoucherStatus status;

    @Schema(description = "전표 유형", example = "GENERAL")
    private VoucherType voucherType;

    @Schema(description = "출처 유형", example = "PAYROLL")
    private SourceType sourceType;

    @Schema(description = "전표 일자", example = "2026-01-15")
    private LocalDate voucherDate;

    @Schema(description = "조회 시작일", example = "2026-01-01")
    private LocalDate startDate;

    @Schema(description = "조회 종료일", example = "2026-01-31")
    private LocalDate endDate;

    @Schema(description = "계정 ID", example = "10")
    private Long accountId;
}

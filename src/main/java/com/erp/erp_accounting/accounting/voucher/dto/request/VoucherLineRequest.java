package com.erp.erp_accounting.accounting.voucher.dto.request;

import com.erp.erp_accounting.accounting.voucher.entity.LineAmount;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전표 라인 요청 DTO")
public class VoucherLineRequest implements LineAmount {

    @Schema(description = "계정 ID", required = true, example = "1")
    @NotNull(message = "계정 미입력")
    private Long accountId;

    @Schema(description = "차변/대변 구분", required = true, example = "DEBIT")
    @NotNull(message = "차변/대변 타입 미입력")
    private LineType type;

    @Schema(description = "금액", required = true, example = "1500000")
    @NotNull(message = "금액 미입력")
    @Positive(message = "금액 0 이하")
    private BigDecimal amount;

    @Override
    public LineType getType() {
        return type;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}

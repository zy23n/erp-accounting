package com.erp.erp_accounting.accounting.voucher.dto.request;

import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherLineRequest {

    @NotNull(message = "계정과목 미입력")
    private Long accountId;

    @NotNull(message = "차변/대변 타입 미입력")
    private LineType type;

    @NotNull(message = "금액 미입력")
    @Positive(message = "금액 0 이하")
    private BigDecimal amount;
}

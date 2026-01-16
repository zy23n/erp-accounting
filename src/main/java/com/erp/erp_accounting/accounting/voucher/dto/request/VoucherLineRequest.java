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

    @NotNull(message = "계정과목은 필수입니다.")
    private Long accountId;

    @NotNull(message = "차변/대변 타입은 필수입니다.")
    private LineType type;

    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 0보다 커야 합니다.")
    private BigDecimal amount;
}

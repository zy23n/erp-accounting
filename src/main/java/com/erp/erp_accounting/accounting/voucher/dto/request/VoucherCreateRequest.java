package com.erp.erp_accounting.accounting.voucher.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCreateRequest {

    @NotNull(message = "전표일자는 필수입니다.")
    private LocalDate voucherDate;

    @Size(max = 500)
    private String description;

    @NotEmpty(message = "전표 라인은 최소 1개 이상 필요합니다.")
    @Valid
    private List<VoucherLineRequest> lines;

    private Long sourceId;
}

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

    @NotNull(message = "전표일자 미입력")
    private LocalDate voucherDate;

    @Size(max = 500, message = "적요 길이 초과")
    private String description;

    @NotEmpty(message = "전표 라인 미존재")
    @Valid
    private List<VoucherLineRequest> lines;
}

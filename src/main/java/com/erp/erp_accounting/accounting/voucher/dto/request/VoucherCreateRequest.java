package com.erp.erp_accounting.accounting.voucher.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "전표 생성 요청 DTO")
public class VoucherCreateRequest {

    @Schema(description = "전표 일자", required = true, example = "2026-01-15")
    @NotNull(message = "전표일자 미입력")
    private LocalDate voucherDate;

    @Schema(description = "적요", example = "1월 급여 지급")
    @Size(max = 500, message = "적요 길이 초과")
    private String description;

    @Schema(description = "전표 라인 목록", required = true)
    @NotEmpty(message = "전표 라인 미존재")
    @Valid
    private List<VoucherLineRequest> lines;
}

package com.erp.erp_accounting.accounting.voucher.service.command;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreateVoucherCommand {

    @NotNull
    private LocalDate voucherDate;

    private String description;

    @NotEmpty
    private List<VoucherLineRequest> lines;

    @NotNull
    private SourceType sourceType;

    private Long sourceId;
}
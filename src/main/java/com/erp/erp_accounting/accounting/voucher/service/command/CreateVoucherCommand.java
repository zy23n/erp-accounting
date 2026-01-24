package com.erp.erp_accounting.accounting.voucher.service.command;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreateVoucherCommand {
    private LocalDate voucherDate;
    private String description;
    private List<VoucherLineRequest> lines;
    private SourceType sourceType;
    private Long sourceId;
}
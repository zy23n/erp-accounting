package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VoucherCommandFixture {

    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 1);

    public static CreateVoucherCommand valid() {
        return new CreateVoucherCommand(
                FIXED_DATE,
                "테스트 전표",
                validLines(1L, 2L),
                SourceType.NONE,
                100L
        );
    }

    private static List<VoucherLineRequest> validLines(Long debitAccountId, Long creditAccountId) {
        return List.of(
                new VoucherLineRequest(debitAccountId, LineType.DEBIT, BigDecimal.valueOf(1000)),
                new VoucherLineRequest(creditAccountId, LineType.CREDIT, BigDecimal.valueOf(1000))
        );
    }
}

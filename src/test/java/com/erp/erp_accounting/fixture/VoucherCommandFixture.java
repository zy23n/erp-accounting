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

    public static CreateVoucherCommand valid(Long debitAccountId, Long creditAccountId) {
        return new CreateVoucherCommand(
                FIXED_DATE,
                "테스트 전표",
                balancedLines(debitAccountId, creditAccountId),
                SourceType.NONE,
                100L
        );
    }

    public static CreateVoucherCommand imbalance(Long debitAccountId, Long creditAccountId) {
        return new CreateVoucherCommand(
                FIXED_DATE,
                "테스트 불일치 전표",
                unbalancedLines(debitAccountId, creditAccountId),
                SourceType.NONE,
                null
        );
    }

    private static List<VoucherLineRequest> balancedLines(Long debitAccountId, Long creditAccountId) {
        return List.of(
                new VoucherLineRequest(debitAccountId, LineType.DEBIT, BigDecimal.valueOf(1_000)),
                new VoucherLineRequest(creditAccountId, LineType.CREDIT, BigDecimal.valueOf(1_000))
        );
    }

    private static List<VoucherLineRequest> unbalancedLines(Long debitAccountId, Long creditAccountId) {
        return List.of(
                new VoucherLineRequest(debitAccountId, LineType.DEBIT, BigDecimal.valueOf(1_000)),
                new VoucherLineRequest(creditAccountId, LineType.CREDIT, BigDecimal.valueOf(500))
        );
    }
}
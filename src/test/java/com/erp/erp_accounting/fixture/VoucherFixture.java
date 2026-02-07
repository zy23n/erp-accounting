package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import com.erp.erp_accounting.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class VoucherFixture {

    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 1);

    public static Voucher draft(User user) {
        return Voucher.builder()
                .voucherNo("V-"  + UUID.randomUUID())
                .voucherDate(FIXED_DATE)
                .status(VoucherStatus.DRAFT)
                .voucherType(VoucherType.GENERAL)
                .sourceType(SourceType.NONE)
                .createdBy(user)
                .build();
    }

    public static Voucher savedDraft(Long id,  User user) {
        Voucher voucher = draft(user);

        ReflectionTestUtils.setField(voucher, "id", id);
        return voucher;
    }

    public static Voucher approvedWithoutValidation(User user) {
        Voucher voucher = draft(user);
        ReflectionTestUtils.setField(voucher, "status", VoucherStatus.APPROVED);
        ReflectionTestUtils.setField(voucher, "approvedBy", user);
        ReflectionTestUtils.setField(voucher, "approvedAt", LocalDateTime.now());
        return voucher;
    }
}
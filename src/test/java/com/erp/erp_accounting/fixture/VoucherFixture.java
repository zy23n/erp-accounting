package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherType;
import com.erp.erp_accounting.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class VoucherFixture {

    public static Voucher draft(User user) {
        return Voucher.builder()
                .voucherNo("V-TEST")
                .voucherDate(LocalDate.now())
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
}
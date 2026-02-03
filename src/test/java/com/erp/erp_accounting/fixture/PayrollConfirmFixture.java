package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import com.erp.erp_accounting.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class PayrollConfirmFixture {

    public static PayrollConfirm created(YearMonth payMonth) {
        PayrollConfirm confirm = PayrollConfirm.builder()
                .payMonth(payMonth)
                .build();
        return confirm;
    }

    public static PayrollConfirm saved(Long id, YearMonth payMonth) {
        PayrollConfirm confirm = created(payMonth);
        ReflectionTestUtils.setField(confirm, "id", id);
        return confirm;
    }

    public static PayrollConfirm confirmed(Long id, YearMonth payMonth, User confirmer) {
        PayrollConfirm confirm = created(payMonth);

        ReflectionTestUtils.setField(confirm, "id", id);
        ReflectionTestUtils.setField(confirm, "status", PayrollConfirmStatus.CONFIRMED);
        ReflectionTestUtils.setField(confirm, "confirmedBy", confirmer);
        ReflectionTestUtils.setField(confirm, "confirmedAt", LocalDateTime.now());
        return confirm;
    }
}
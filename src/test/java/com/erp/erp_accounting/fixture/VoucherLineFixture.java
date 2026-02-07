package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherLine;

import java.math.BigDecimal;

public class VoucherLineFixture {

    public static void addBalancedLines(Voucher voucher) {
        voucher.addLine(debitLine(AccountFixture.cash(), 1_000));
        voucher.addLine(creditLine(AccountFixture.revenue(), 1_000));
    }

    public static void addUnbalancedLines(Voucher voucher) {
        voucher.addLine(debitLine(AccountFixture.cash(), 1_000));
        voucher.addLine(creditLine(AccountFixture.revenue(), 500));
    }

    public static void addBalancedLines(Voucher voucher, Account debitAccount, Account creditAccount) {
        voucher.addLine(debitLine(debitAccount, 1_000));
        voucher.addLine(creditLine(creditAccount, 1_000));
    }

    public static void addUnbalancedLines(Voucher voucher, Account debitAccount, Account creditAccount) {
        voucher.addLine(debitLine(debitAccount, 1_000));
        voucher.addLine(creditLine(creditAccount, 500));
    }

    private static VoucherLine debitLine(Account account, int amount) {
        return VoucherLine.builder()
                .account(account)
                .type(LineType.DEBIT)
                .amount(BigDecimal.valueOf(amount))
                .build();
    }

    private static VoucherLine creditLine(Account account, int amount) {
        return VoucherLine.builder()
                .account(account)
                .type(LineType.CREDIT)
                .amount(BigDecimal.valueOf(amount))
                .build();
    }

    public static void addDebitOnly(Voucher voucher, Account debitAccount, BigDecimal amount) {
        voucher.addLine(VoucherLine.builder()
                .account(debitAccount)
                .type(LineType.DEBIT)
                .amount(amount)
                .build());
    }

    public static void addCreditOnly(Voucher voucher, Account creditAccount, BigDecimal amount) {
        voucher.addLine(VoucherLine.builder()
                .account(creditAccount)
                .type(LineType.CREDIT)
                .amount(amount)
                .build());
    }
}

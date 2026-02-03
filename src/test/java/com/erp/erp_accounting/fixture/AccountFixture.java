package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.AccountCategory;
import org.springframework.test.util.ReflectionTestUtils;

public class AccountFixture {

    public static Account cash() {
        return leafAccount(1L, "1000", "현금", AccountCategory.ASSET);
    }

    public static Account revenue() {
        return leafAccount(2L, "4000", "매출", AccountCategory.REVENUE);
    }

    public static Account expense() {
        return leafAccount(3L, "5000", "비용", AccountCategory.EXPENSE);
    }

    private static Account leafAccount(Long id, String code, String name, AccountCategory category) {
        Account account = Account.builder()
                .code(code)
                .name(name)
                .category(category)
                .build();

        ReflectionTestUtils.setField(account, "id", id);
        ReflectionTestUtils.setField(account, "leaf", true);
        return account;
    }
}
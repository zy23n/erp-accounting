package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.YearMonth;

public class MonthlyAccountBalanceFixture {

    public static MonthlyAccountBalance snapshot(Long id, Account account, YearMonth period, String closingBalance) {
        MonthlyAccountBalance balance = MonthlyAccountBalance.builder()
                .account(account)
                .period(period)
                .openingBalance(Money.zero())
                .debitSum(Money.zero())
                .creditSum(Money.zero())
                .closingBalance(Money.of(closingBalance))
                .normalBalance(account.getNormalBalance())
                .build();

        ReflectionTestUtils.setField(balance, "id", id);
        return balance;
    }
}
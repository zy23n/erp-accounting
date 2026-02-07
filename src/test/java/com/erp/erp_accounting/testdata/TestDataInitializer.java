package com.erp.erp_accounting.testdata;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.AccountCategory;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import com.erp.erp_accounting.accounting.period.repository.AccountingPeriodRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.entity.UserRole;
import com.erp.erp_accounting.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Set;

@Profile("test")
@Component
@RequiredArgsConstructor
@Transactional
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountingPeriodRepository accountingPeriodRepository;

    @PostConstruct
    public void init() {
        initUsers();
        initAccounts();
        initAccountingPeriods();
    }

    // User
    private void initUsers() {
        if (userRepository.findByUsername("admin").isPresent()
                && userRepository.findByUsername("accounting").isPresent()
                && userRepository.findByUsername("hr").isPresent()
                && userRepository.findByUsername("normal").isPresent()) {
            return;
        }

        userRepository.deleteAll();

        User adminUser = User.builder()
                .username("admin")
                .password("{noop}password")
                .roles(Set.of(UserRole.ADMIN))
                .build();

        userRepository.save(adminUser);

        User accountingUser = User.builder()
                .username("accounting")
                .password("{noop}password")
                .roles(Set.of(UserRole.USER, UserRole.ACCOUNTING))
                .build();

        userRepository.save(accountingUser);

        User hrUser = User.builder()
                .username("hr")
                .password("{noop}password")
                .roles(Set.of(UserRole.USER, UserRole.HR))
                .build();

        userRepository.save(hrUser);

        User normalUser = User.builder()
                .username("normal")
                .password("{noop}password")
                .roles(Set.of(UserRole.USER))
                .build();

        userRepository.save(normalUser);
    }

    // Account
    private void initAccounts() {
        if (accountRepository.count() > 0) return;

        // === 자산 ===
        saveAccount("1010", "현금", AccountCategory.ASSET);         // CASH
        saveAccount("1020", "보통예금", AccountCategory.ASSET);      // BANK_TRANSFER

        // === 부채 ===
        saveAccount("2020", "예수금", AccountCategory.LIABILITY);    // DEDUCTION

        // === 수익 ===
        saveAccount("4000", "매출", AccountCategory.REVENUE);

        // === 비용 ===
        saveAccount("5010", "급여", AccountCategory.EXPENSE);        // BASE_SALARY
        saveAccount("5020", "상여", AccountCategory.EXPENSE);        // BONUS
    }

    private void saveAccount(String code, String name, AccountCategory category) {
        Account account = Account.builder()
                .code(code)
                .name(name)
                .category(category)
                .build();

        accountRepository.save(account);
    }

    // Accounting Period
    private void initAccountingPeriods() {
        YearMonth now = YearMonth.of(2026, 1);
        YearMonth prev = now.minusMonths(1);

        if (accountingPeriodRepository.findByPeriod(now).isEmpty()) {
            accountingPeriodRepository.save(AccountingPeriod.open(now));
        }

        if (accountingPeriodRepository.findByPeriod(prev).isEmpty()) {
            AccountingPeriod closed = AccountingPeriod.open(prev);
            closed.close(null);
            accountingPeriodRepository.save(closed);
        }
    }
}
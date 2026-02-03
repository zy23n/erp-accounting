package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.entity.UserRole;

import java.util.Set;

public class UserFixture {

    public static User adminUser() {
        return User.builder()
                .username("admin")
                .password("pw")
                .roles(Set.of(UserRole.ADMIN))
                .build();
    }

    public static User accountingUser() {
        return User.builder()
                .username("accounting")
                .password("pw")
                .roles(Set.of(UserRole.ACCOUNTING, UserRole.USER))
                .build();
    }

    public static User hrUser() {
        return User.builder()
                .username("hr")
                .password("pw")
                .roles(Set.of(UserRole.HR, UserRole.USER))
                .build();
    }

    public static User normalUser() {
        return User.builder()
                .username("user")
                .password("pw")
                .roles(Set.of(UserRole.USER))
                .build();
    }
}

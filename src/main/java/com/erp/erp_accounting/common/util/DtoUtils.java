package com.erp.erp_accounting.common.util;

import com.erp.erp_accounting.user.entity.User;

public final class DtoUtils {

    private DtoUtils() {}

    public static Long getUserId(User user) {
        return user != null ? user.getId() : null;
    }

    public static String getUsername(User user) {
        return user != null ? user.getUsername() : null;
    }
}
package com.erp.erp_accounting.common.util;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Objects;

public class QuerydslUtils {

    public static OrderSpecifier<?>[] toOrderSpecifiers(
            Pageable pageable, Map<String, ComparableExpressionBase<?>> sortMap
    ) {
        return pageable.getSort().stream()
                .map(order -> {
                    ComparableExpressionBase<?> path = sortMap.get(order.getProperty());
                    if (path == null) return null;

                    return order.isAscending() ? path.asc() : path.desc();
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }
}

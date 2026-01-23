package com.erp.erp_accounting.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorResponse {
    private final String field;
    private final Object value;
    private final String reason;

    public static FieldErrorResponse of(String field, Object value, String reason) {
        return new FieldErrorResponse(field, value, reason);
    }
}

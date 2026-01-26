package com.erp.erp_accounting.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final String detailMessage;
    private final List<FieldErrorResponse> errors;

    // BusinessException 전용
    public static ErrorResponse from(BusinessException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getErrorCode().getMessage(),
                e.hasDetail() ? e.getDetailMessage() : null,
                List.of()
        );
    }

    // Validation 에러 전용
    public static ErrorResponse validation(List<FieldErrorResponse> errors) {
        return new ErrorResponse(
                ErrorCode.INVALID_REQUEST.getCode(),
                ErrorCode.INVALID_REQUEST.getMessage(),
                null,
                errors
        );
    }

    // 일반 Exception 전용
    public static ErrorResponse unexpected() {
        return new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null,
                List.of()
        );
    }
}

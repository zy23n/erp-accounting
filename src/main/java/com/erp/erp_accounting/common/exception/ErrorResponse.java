package com.erp.erp_accounting.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private final String detailMessage;

    public static ErrorResponse from(BusinessException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getErrorCode().getMessage(),
                e.getDetailMessage()
        );
    }
}

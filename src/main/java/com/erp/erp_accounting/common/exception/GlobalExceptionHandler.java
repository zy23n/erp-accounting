package com.erp.erp_accounting.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        log.warn("[EXCEPTION] action=BUSINESS, code={}, message={}", e.getCode(), e.getDetailMessage(), e);

        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.from(e));
    }

    // 파라미터 검증 에러 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

        List<FieldErrorResponse> errors =
                e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(err -> new FieldErrorResponse(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                        .toList();

        log.warn("[EXCEPTION] action=VALIDATION, errors={}", errors);

        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus()).body(ErrorResponse.validation(errors));
    }

    // 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("[EXCEPTION] action=UNEXPECTED, message={}", e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null,
                null
        );

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }
}
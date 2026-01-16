package com.erp.erp_accounting.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        log.warn("BusinessException 발생: {} - {}", e.getCode(), e.getDetailMessage(), e);

        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.from(e));
    }

    // 파라미터 검증 에러 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("ValidationException 발생: {}", message, e);

        ErrorResponse response = new ErrorResponse("VALIDATION_ERROR", "요청 파라미터 검증 실패", message);

        return ResponseEntity.badRequest().body(response);
    }

    // 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("알 수 없는 예외 발생", e);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
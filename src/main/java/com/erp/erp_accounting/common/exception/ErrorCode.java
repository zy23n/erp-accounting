package com.erp.erp_accounting.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== COMMON =====
    INVALID_REQUEST("COMMON_400", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND("COMMON_404", HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE("COMMON_409", HttpStatus.CONFLICT, "이미 존재하는 데이터입니다."),
    INTERNAL_SERVER_ERROR("COMMON_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // ===== STATE =====
    INVALID_STATE("STATE_400", HttpStatus.BAD_REQUEST, "현재 상태에서는 해당 작업을 수행할 수 없습니다."),

    // ===== PERIOD ====
    PERIOD_NOT_CLOSED("PERIOD_409", HttpStatus.CONFLICT, "이전 회계기간이 마감되지 않았습니다."),
    PERIOD_ALREADY_CLOSED("PERIOD_409_2", HttpStatus.CONFLICT, "이미 마감된 회계기간입니다."),

    // ===== ACCOUNTING =====
    IMBALANCE_AMOUNT("ACCOUNT_400", HttpStatus.BAD_REQUEST, "대차 또는 금액 합계가 일치하지 않습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}

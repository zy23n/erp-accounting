package com.erp.erp_accounting.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "Access Token")
    private String accessToken;

    @Schema(description = "Refresh Token")
    private String refreshToken;

    @Schema(description = "사용자 계정명", example = "accounting_admin")
    private String username;

    @Schema(description = "사용자 권한 목록", example = "[\"ROLE_ACCOUNTING\", \"ROLE_ADMIN\"]")
    private List<String> roles;
}
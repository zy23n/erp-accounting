package com.erp.erp_accounting.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Access Token 재발급 응답 DTO")
public class RefreshTokenResponse {

    @Schema(description = "새로 발급된 Access Token")
    private String accessToken;

    @Schema(description = "기존 Refresh Token")
    private String refreshToken;
}
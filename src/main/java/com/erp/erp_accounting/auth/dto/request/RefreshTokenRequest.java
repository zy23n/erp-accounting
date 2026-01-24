package com.erp.erp_accounting.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Refresh Token 요청 DTO")
public class RefreshTokenRequest {

    @Schema(description = "Refresh Token", example = "c3fee52c-be46-4cff-88ad-594...", required = true)
    @NotBlank(message = "Refresh Token 미입력")
    private String refreshToken;
}

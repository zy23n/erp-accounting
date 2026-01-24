package com.erp.erp_accounting.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "사용자 계정명", example = "accounting_admin", required = true)
    @NotBlank(message = "아이디 미입력")
    private String username;

    @Schema(description = "비밀번호", example = "password123!", required = true)
    @NotBlank(message = "비밀번호 미입력")
    private String password;
}
package com.erp.erp_accounting.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "아이디 미입력")
    private String username;

    @NotBlank(message = "비밀번호 미입력")
    private String password;
}
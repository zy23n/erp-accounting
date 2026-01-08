package com.erp.erp_accounting.security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String username;
    private List<String> roles;
}
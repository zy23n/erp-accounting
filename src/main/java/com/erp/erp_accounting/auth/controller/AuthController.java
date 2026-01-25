package com.erp.erp_accounting.auth.controller;

import com.erp.erp_accounting.auth.dto.request.LoginRequest;
import com.erp.erp_accounting.auth.dto.request.RefreshTokenRequest;
import com.erp.erp_accounting.auth.dto.response.LoginResponse;
import com.erp.erp_accounting.auth.dto.response.RefreshTokenResponse;
import com.erp.erp_accounting.auth.entity.RefreshToken;
import com.erp.erp_accounting.auth.service.RefreshTokenService;
import com.erp.erp_accounting.security.annotation.CurrentUser;
import com.erp.erp_accounting.security.jwt.JwtTokenProvider;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import com.erp.erp_accounting.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인 및 토큰 기반 인증 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 Access Token과 Refresh Token을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[AUTH] action=LOGIN_REQUEST, username={}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        log.info("[AUTH] action=LOGIN_SUCCESS, userId={}", principal.getUser().getId());

        String accessToken = tokenProvider.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal.getUser());;
        String username = principal.getUsername();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken.getToken(), username, roles));
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("[AUTH] action=REFRESH_TOKEN_REQUEST");

        // Refresh Token 검증
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();
        log.info("[AUTH] action=REFRESH_TOKEN_ISSUED, userId={}", user.getId());
        UserPrincipal principal = new UserPrincipal(user);

        // 새로운 Access Token 발급
        String newAccessToken = tokenProvider.generateAccessToken(principal);

        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, refreshToken.getToken()));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 특정 Refresh Token을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequest request, @CurrentUser User user) {
        log.info("[AUTH] action=LOGOUT_REQUEST, userId={}", user.getId());

        refreshTokenService.logout(request.getRefreshToken(), user);

        log.info("[AUTH] action=LOGOUT_COMPLETE, userId={}", user.getId());
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    @Operation(summary = "전체 로그아웃", description = "현재 사용자와 연관된 모든 Refresh Token을 삭제하여 모든 기기에서 로그아웃합니다.")
    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutAll(@CurrentUser User user) {
        log.info("[AUTH] action=LOGOUT_ALL_REQUEST, userId={}", user.getId());

        refreshTokenService.deleteAllByUser(user);

        log.info("[AUTH] action=LOGOUT_ALL_COMPLETE, userId={}", user.getId());
        return ResponseEntity.ok(Map.of("message", "모든 기기 로그아웃 완료"));
    }
}
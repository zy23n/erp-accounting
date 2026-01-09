package com.erp.erp_accounting.auth.service;

import com.erp.erp_accounting.auth.entity.RefreshToken;
import com.erp.erp_accounting.auth.repository.RefreshTokenRepository;
import com.erp.erp_accounting.security.jwt.JwtTokenProvider;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration-days}")
    private long refreshTokenExpirationDays;

    // Refresh Token 생성 + 저장
    public RefreshToken createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // Refresh Token 검증
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh Token 없음"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token 만료");
        }

        return refreshToken;
    }

    // 특정 Refresh Token 삭제
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    // 전체 Refresh Token 삭제
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }
}


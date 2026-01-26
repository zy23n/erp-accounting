package com.erp.erp_accounting.auth.service;

import com.erp.erp_accounting.auth.entity.RefreshToken;
import com.erp.erp_accounting.auth.repository.RefreshTokenRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

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

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        log.info("[AUTH] action=REFRESH_TOKEN_CREATED, userId={}, expiresAt={}", user.getId(), saved.getExpiresAt());

        return saved;
    }

    // Refresh Token 검증
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Refresh Token 미존재"));

        if (refreshToken.isExpired()) {
            log.warn("[AUTH] action=REFRESH_TOKEN_EXPIRED, userId={}", refreshToken.getUser().getId());
            throw new BusinessException(ErrorCode.INVALID_STATE, "RefreshToken 만료");
        }

        log.info("[AUTH] action=REFRESH_TOKEN_VALIDATED, userId={}", refreshToken.getUser().getId());

        return refreshToken;
    }

    // 특정 Refresh Token 삭제
    public void logout(String refreshToken, User user) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Refresh Token 미존재"));

        if (!token.getUser().getId().equals(user.getId())) {
            log.warn("[AUTH] action=LOGOUT_DENIED, tokenUserId={}, requestUserId={}", token.getUser().getId(), user.getId());
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Refresh Token 소유자 불일치");
        }

        refreshTokenRepository.delete(token);

        log.info("[AUTH] action=LOGOUT, userId={}", user.getId());
    }

    // 전체 Refresh Token 삭제
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
        log.info("[AUTH] action=LOGOUT_ALL, userId={}", user.getId());
    }
}


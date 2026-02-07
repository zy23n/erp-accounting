package com.erp.erp_accounting.auth.service;

import com.erp.erp_accounting.auth.entity.RefreshToken;
import com.erp.erp_accounting.auth.repository.RefreshTokenRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Refresh Token 서비스 테스트")
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.normalUser();
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationDays", 7L);
    }

    @Test
    @DisplayName("Refresh Token 만료 시 예외")
    void validateRefreshToken_expired() {
        // given
        RefreshToken expiredToken = RefreshToken.builder()
                .user(user)
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(refreshTokenRepository.findByToken("expired-token")).willReturn(Optional.of(expiredToken));

        // when & then
        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("expired-token"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
                });
    }

    @Test
    @DisplayName("유효한 Refresh Token 검증 성공")
    void validateRefreshToken_success() {
        RefreshToken validToken = RefreshToken.builder()
                .user(user)
                .token("valid-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        given(refreshTokenRepository.findByToken("valid-token")).willReturn(Optional.of(validToken));

        RefreshToken result = refreshTokenService.validateRefreshToken("valid-token");

        assertThat(result).isEqualTo(validToken);
    }
}
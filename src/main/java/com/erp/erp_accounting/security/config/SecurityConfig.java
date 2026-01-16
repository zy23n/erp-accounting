package com.erp.erp_accounting.security.config;

import com.erp.erp_accounting.common.exception.ErrorResponse;
import com.erp.erp_accounting.security.jwt.JwtAuthenticationFilter;
import com.erp.erp_accounting.security.jwt.JwtExceptionHandlerFilter;
import com.erp.erp_accounting.security.jwt.JwtTokenProvider;
import com.erp.erp_accounting.security.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // UserDetailsService + PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public JwtExceptionHandlerFilter jwtExceptionHandlerFilter() {
        return new JwtExceptionHandlerFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   JwtExceptionHandlerFilter jwtExceptionHandlerFilter) throws Exception {

        http
                // JWT 기반 stateless API
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                                .requestMatchers("/api/auth/logout/**").authenticated()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/closing/**").hasRole("ADMIN")

                                // ACCOUNTING
                                .requestMatchers(HttpMethod.GET, ApiRoles.ACCOUNTING_APIS).hasAnyRole("ACCOUNTING", "ADMIN")

                                // ACCOUNTING
                                .requestMatchers(HttpMethod.POST, ApiRoles.ACCOUNTING_APIS).hasRole("ACCOUNTING")
                                .requestMatchers(HttpMethod.PATCH, ApiRoles.ACCOUNTING_APIS).hasRole("ACCOUNTING")
                                .requestMatchers(HttpMethod.PUT, ApiRoles.ACCOUNTING_APIS).hasRole("ACCOUNTING")
                                .requestMatchers(HttpMethod.DELETE, ApiRoles.ACCOUNTING_APIS).hasRole("ACCOUNTING")

                                // HR
                                .requestMatchers(HttpMethod.GET, ApiRoles.HR_APIS).authenticated()

                                // HR
                                .requestMatchers(HttpMethod.POST, ApiRoles.HR_APIS).hasRole("HR")
                                .requestMatchers(HttpMethod.PATCH, ApiRoles.HR_APIS).hasRole("HR")
                                .requestMatchers(HttpMethod.PUT, ApiRoles.HR_APIS).hasRole("HR")
                                .requestMatchers(HttpMethod.DELETE, ApiRoles.HR_APIS).hasRole("HR")

                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionHandlerFilter, JwtAuthenticationFilter.class)

                // 인증 Provider
                .authenticationProvider(authenticationProvider())

                // 예외처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 실패"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "권한 없음"))
                );

        return http.build();
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                status == 401 ? "UNAUTHORIZED" : "FORBIDDEN",
                message,
                null
        );

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
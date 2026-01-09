package com.erp.erp_accounting.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED, "토큰 만료");
        } catch (JwtException | IllegalArgumentException e) {
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
        } catch (AuthenticationException e) {
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpServletRequest request,
                                   int status, String message) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "error", "Unauthorized",
                "message", message,
                "path", request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}

package com.erp.erp_accounting.security.jwt;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, ErrorCode.UNAUTHORIZED, "토큰 만료");
        } catch (JwtException | IllegalArgumentException e) {
            writeErrorResponse(response, ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰");
        }

    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, String detailMessage) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(new BusinessException(errorCode, detailMessage));

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}

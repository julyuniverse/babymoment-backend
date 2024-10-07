package com.benection.babymoment.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    final private FilterErrorResponse filterErrorResponse;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String tokenException = (String) request.getAttribute("Token-Exception");
        if (StringUtils.hasText(tokenException)) {
            if (tokenException.equals(ErrorCode.EXPIRED_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.EXPIRED_TOKEN);
            } else if (tokenException.equals(ErrorCode.INVALID_SIGNATURE_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.INVALID_SIGNATURE_TOKEN);
            } else if (tokenException.equals(ErrorCode.DECODING_FAILED_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.DECODING_FAILED_TOKEN);
            } else if (tokenException.equals(ErrorCode.VERIFICATION_FAILED_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.VERIFICATION_FAILED_TOKEN);
            } else if (tokenException.equals(ErrorCode.MISSING_AUTHORITIES_CLAIM.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.MISSING_AUTHORITIES_CLAIM);
            } else if (tokenException.equals(ErrorCode.NOT_ACCESS_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NOT_ACCESS_TOKEN);
            } else if (tokenException.equals(ErrorCode.NOT_REFRESH_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NOT_REFRESH_TOKEN);
            } else if (tokenException.equals(ErrorCode.MISSING_TOKEN_TYPE_CLAIM.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.MISSING_TOKEN_TYPE_CLAIM);
            } else if (tokenException.equals(ErrorCode.MISSING_DEVICE_ID.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.MISSING_DEVICE_ID);
            } else if (tokenException.equals(ErrorCode.LOGGED_OUT_ACCOUNT.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.LOGGED_OUT_ACCOUNT);
            } else if (tokenException.equals(ErrorCode.MISSING_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.MISSING_TOKEN);
            } else if (tokenException.equals(ErrorCode.MISMATCH_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.MISMATCH_TOKEN);
            } else if (tokenException.equals(ErrorCode.FAILURE.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.FAILURE);
            }
        } else {
            log.info("[commence] Token-Exception 헤더 값이 없어요.");
            filterErrorResponse.setResponse(response, ErrorCode.FAILURE);
        }
    }
}

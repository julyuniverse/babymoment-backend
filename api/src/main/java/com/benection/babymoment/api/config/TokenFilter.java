package com.benection.babymoment.api.config;

import com.benection.babymoment.api.enums.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.benection.babymoment.api.util.HttpHeaderUtils.getAuthorizationToken;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * 실제 필터링 로직은 doFilterInternal 메서드에 들어간다.
     * 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 객체에 저장하는 역할을 수행한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 1. Get the authentication token from Authentication header value.
        String token = getAuthorizationToken();
        try {
            // 2. Check if the token is null or empty.
            if (StringUtils.hasText(token)) {
                // 3. Validate access token.
                tokenProvider.validateToken(token, TokenType.ACCESS, true);

                // 4. redis 블랙 리스트에서 logout된 access token이 있는지 확인한다.
                String loggedOutToken = redisService.getData(token);
                if (!ObjectUtils.isEmpty(loggedOutToken)) { // logout된 access token이 존재한다면
                    request.setAttribute("Token-Exception", ErrorCode.LOGGED_OUT_ACCOUNT.name());
                } else {
                    // 클레임 정보 검사 (권한, 토큰 타입 등등..)
                    // 정상 토큰이면 해당 토큰으로 Authentication 객체을 가져와서 SecurityContext 객체에 저장한다.
                    Authentication authentication = tokenProvider.getAuthentication(token, TokenType.ACCESS);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                request.setAttribute("Token-Exception", ErrorCode.MISSING_TOKEN.name());
            }
        } catch (CustomException e) {
            log.info("[doFilterInternal] ErrorCode: " + e.getErrorCode());
            request.setAttribute("Token-Exception", e.getErrorCode().name());
        } catch (Exception e) {
            request.setAttribute("Token-Exception", ErrorCode.FAILURE.name());
            log.error("[doFilterInternal] ================================================");
            log.error("[doFilterInternal] doFilterInternal() error occurred");
            log.error("[doFilterInternal] token: {}", token);
            log.error("[doFilterInternal] Exception Message: {}", e.getMessage());
            log.error("[doFilterInternal] Exception StackTrace: {", e);
            log.error("}");
            log.error("[doFilterInternal] ================================================");
        }
        filterChain.doFilter(request, response);
    }
}

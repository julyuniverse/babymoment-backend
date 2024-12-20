package com.benection.babymoment.api.config;

import com.benection.babymoment.api.service.VersionService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static com.benection.babymoment.api.util.DateUtils.convertLocalTimeToKoreanTime;

/**
 * spring security에서 토큰을 먼저 인증하고 난 이후 http header 인증을 진행한다.
 *
 * @author Lee Taesung
 * @since 1.0
 */
@WebFilter(urlPatterns = "/*")
@RequiredArgsConstructor
@Slf4j
public class HttpHeaderFilter implements Filter {
    private final FilterErrorResponse filterErrorResponse;
    private final VersionService versionService;
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String DATETIME_OFFSET = "Datetime-Offset";
    public static final String TIMEZONE_IDENTIFIER = "Timezone-Identifier";
    public static final String APP_VERSION = "App-Version";
    public static final String PLATFORM = "Platform";
    public static final String DEVICE_ID = "Device-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // Get Accept-Language header value.
        String acceptLanguage = httpServletRequest.getHeader(ACCEPT_LANGUAGE);
        if (!StringUtils.hasText(acceptLanguage)) {
            log.info("[doFilter] Accept-Language 헤더 값이 없어요.");
            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_ACCEPT_LANGUAGE);
            return;
        }

        // Get Datetime-Offset header value.
        String datetimeOffset = httpServletRequest.getHeader(DATETIME_OFFSET);
        if (StringUtils.hasText(datetimeOffset)) {
            try {
                // ISO-8601 형식을 확인한다.
                OffsetDateTime offsetDatetime = OffsetDateTime.parse(datetimeOffset).withNano(0);
                LocalDateTime koreanTime = convertLocalTimeToKoreanTime(offsetDatetime);

                // 현재 시각을 가져온다.
                OffsetDateTime currentDatetime = OffsetDateTime.now().withNano(0);

                // 현재 시스템 시각과의 차이를 계산한다.
                Duration duration = Duration.between(koreanTime, currentDatetime).abs();

                // 오차 범위가 60분을 초과하는지 확인한다.
                if (duration.toMinutes() > 60) {
                    log.info("[doFilter] Datetime-Offset header 값이 현재 시각과 60분을 초과한 오차가 있습니다.");
                    filterErrorResponse.setResponse(httpServletResponse, ErrorCode.INVALID_OFFSET_DATETIME_RANGE);
                    return;
                }
            } catch (DateTimeParseException e) {
                log.info("[doFilter] ParsingError: Datetime-Offset header 값이 Offset Datetime 형식이 아니에요.");
                filterErrorResponse.setResponse(httpServletResponse, ErrorCode.INVALID_FORMAT_OFFSET_DATETIME);
                return;
            }
        } else {
            log.info("[doFilter] Datetime-Offset 헤더 값이 없어요.");
            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_OFFSET_DATETIME);
            return;
        }

        // Get Timezone-identifier header value.
        String timezoneIdentifier = httpServletRequest.getHeader(TIMEZONE_IDENTIFIER);
        if (!StringUtils.hasText(timezoneIdentifier)) {
            log.info("[doFilter] Timezone-Identifier 헤더 값이 없어요.");
            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_TIMEZONE_IDENTIFIER);
            return;
        }

        // App-Version 뽑아내기.
        String appVersion = httpServletRequest.getHeader(APP_VERSION);
        if (!StringUtils.hasText(appVersion)) {
            log.info("[doFilter] App-Version 헤더 값이 없어요.");
            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_APP_VERSION);
            return;
        }

        // Platform 뽑아내기.
        String platform = httpServletRequest.getHeader(PLATFORM);
        if (!StringUtils.hasText(platform)) {
            log.info("[doFilter] Platform 헤더 값이 없어요.");
            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_PLATFORM);
            return;
        }

        // Device-Id 뽑아내기.
        long deviceId = NumberUtils.toLong(httpServletRequest.getHeader(DEVICE_ID), 0L);
        if (!Pattern.matches("/v[0-9]/auth/login/uuid", httpServletRequest.getRequestURI())) { // uuidLogin api는 제외한다.
            if (deviceId <= 0L) {
                log.info("[doFilter] Device-Id 헤더 값이 없어요.");
                filterErrorResponse.setResponse(httpServletResponse, ErrorCode.MISSING_DEVICE_ID);
                return;
            }
        }

        // 버전 체크하기.
//        if (appVersionService.isUpdateMandatory(platform, appVersion)) {
//            log.info("[doFilter] 클라이언트 앱 버전이 오래되었어요. 업데이트 필요.");
//            filterErrorResponse.setResponse(httpServletResponse, ErrorCode.UPDATE_REQUIRED_APP_VERSION);
//            return;
//        }
        chain.doFilter(httpServletRequest, httpServletResponse);
    }
}

package com.benection.babymoment.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class HttpHeaderUtils {
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String DATETIME_OFFSET_HEADER = "Datetime-Offset";
    private static final String TIMEZONE_IDENTIFIER_HEADER = "Timezone-Identifier";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PLATFORM_HEADER = "Platform";
    private static final String DEVICE_ID_HEADER = "Device-ID";
    private static final String APP_VERSION_HEADER = "App-Version";

    /**
     * @return ip address
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getIpAddress() {
        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            return "0.0.0.0";
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header : IP_HEADER_CANDIDATES) {
            String ipFromHeader = request.getHeader(header);
            if (Objects.nonNull(ipFromHeader) && !ipFromHeader.isEmpty() && !"unknown".equalsIgnoreCase(ipFromHeader)) {
                return ipFromHeader.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * @return user agent
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getUserAgent() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return request.getHeader(USER_AGENT_HEADER);
    }

    /**
     * @return authorization token
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getAuthorizationToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * @return datetime offset
     * @author Lee Taesung
     * @since 1.0
     */
    public static OffsetDateTime getDatetimeOffset() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return OffsetDateTime.parse(request.getHeader(DATETIME_OFFSET_HEADER)).withNano(0);
    }

    /**
     * @return timezone identifier
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getTimezoneIdentifier() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return request.getHeader(TIMEZONE_IDENTIFIER_HEADER);
    }

    /**
     * @return platform
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getPlatform() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return request.getHeader(PLATFORM_HEADER);
    }

    /**
     * @return device id
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getDeviceId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return request.getHeader(DEVICE_ID_HEADER);
    }

    /**
     * @return app version
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getAppVersion() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return request.getHeader(APP_VERSION_HEADER);
    }
}

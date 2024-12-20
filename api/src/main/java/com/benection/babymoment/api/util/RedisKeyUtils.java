package com.benection.babymoment.api.util;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class RedisKeyUtils {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static String generateRefreshTokenKey(String refreshToken) {
        return String.format("refresh_token:%s", refreshToken);
    }
}

package com.benection.babymoment.api.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Slf4j
public class RandomUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * @param length 문자 길이
     * @return 램던 영어 대문자
     * @author Lee Taesung
     * @since 1.0
     */
    public static String randomEnglishUppercase(int length) {
        int leftLimit = 65;
        int rightLimit = 90;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                //                .filter(i -> (i != 73) && (i != 79)) // 대문자 I, O 제거
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * @param length 문자 길이
     * @return 램덤 영어 소문자
     * @author Lee Taesung
     * @since 1.0
     */
    public static String randomEnglishLowercase(int length) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * @param length 문자 길이
     * @return 램덤 영어
     * @author Lee Taesung
     * @since 1.0
     */
    public static String randomEnglish(int length) {
        int leftLimit = 65;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> !((i >= 91) && (i <= 96)))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * @param length 문자 길이
     * @return 램덤 영어, 숫자
     * @author Lee Taesung
     * @since 1.0
     */
    public static String randomEnglishAndNumber(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> !((i >= 58) && (i <= 64)) && !((i >= 91) && (i <= 96)))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * @param length 숫자 길이
     * @return 램덤 숫자
     * @author Lee Taesung
     * @since 1.0
     */
    public static Integer randomNumber(int length) {
        // int 최댓값 2,147,483,647
        if (length > 10) { // 파라미터 length가 10자리를 초과했다면 해당 메서드 사용 불가
            log.info("[randomNumber] int 최대값 초과");
            return null;
        }
        int min = Integer.parseInt("1" + "0".repeat(Math.max(0, (length - 1))));
        int max;
        if (length == 10) { // 파라미터 length가 10자리라면 최댓값 2,147,483,647로 설정
            max = 2147483647;
        } else {
            max = Integer.parseInt("9" + "9".repeat(Math.max(0, (length - 1))));
        }

        return (int) (Math.random() * (max - min + 1) + min);
    }

    /**
     * @param length 길이
     * @author Lee Taesung
     * @since 1.0
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer.");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }

        return sb.toString();
    }
}

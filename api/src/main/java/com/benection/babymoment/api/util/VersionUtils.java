package com.benection.babymoment.api.util;

import java.util.Arrays;
import java.util.List;

/**
 * 2개의 .으로 이루어진 버전(ex: 1.9.0)<br/>
 * ip 숫자 계산 방식으로 값을 반환한다.
 *
 * @author Lee Taesung
 * @since 1.0
 */
public class VersionUtils {
    private static final Long MULTIPLICATION_256_3 = 256L * 256L * 256L;
    private static final Long MULTIPLICATION_256_2 = 256L * 256L;

    public static Long toNumber(String version) {
        if (version == null) {
            return null;
        }
        List<Integer> split = Arrays.stream(version.split("\\.")).map(Integer::parseInt).toList();
        if (split.isEmpty()) {
            return null;
        }
        if (split.size() == 3) {
            return (split.get(0) * MULTIPLICATION_256_3) + (split.get(1) * MULTIPLICATION_256_2) + split.get(2);
        } else {
            return null;
        }
    }

    public static String toDot(Long number) {
        if (number == null) {
            return null;
        }
        String c = String.valueOf(number % 256);
        String b = String.valueOf((number / (MULTIPLICATION_256_2)) % 256);
        String a = String.valueOf((number / (MULTIPLICATION_256_3)) % 256);

        return a + "." + b + "." + c;
    }
}

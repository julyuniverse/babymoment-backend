package com.benection.babymoment.api.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * 이 애플리케이션과 서버의 UTC time 기준은 Asia/Seoul이다. (UTC + 09:00)
 *
 * @author Lee Taesung
 * @since 1.0
 */
public class DateUtils {

    /**
     * @param offsetDatetime 현지 offset datetime
     * @return 현지 utc offset -> 한국 시각
     * @author Lee Taesung
     * @since 1.0
     */
    public static LocalDateTime applyUtcOffsetToKoreanTime(OffsetDateTime offsetDatetime, LocalDateTime datetime) {
        int offsetHours = offsetDatetime.getOffset().getTotalSeconds() / 60 / 60;
        int hours = offsetHours - 9;

        return datetime.minusHours(hours);
    }

    /**
     * @param offsetDatetime 현지 offset datetime
     * @return 현지 utc offset을 이용해 한국 시각 -> 현지 시각
     * @author Lee Taesung
     * @since 1.0
     */
    public static LocalDateTime applyUtcOffsetToLocalTime(OffsetDateTime offsetDatetime, LocalDateTime datetime) {
        int offsetHours = offsetDatetime.getOffset().getTotalSeconds() / 60 / 60;
        int hours = 9 - offsetHours;

        return datetime.minusHours(hours);
    }

    /**
     * @param offsetDatetime 현지 offset datetime
     * @return 한국 시각을 현지 시각으로 리턴한다.
     * @author Lee Taesung
     * @since 1.0
     */
    public static LocalDate applyUtcOffsetToLocalDate(OffsetDateTime offsetDatetime, LocalDateTime datetime) {
        int offsetHours = offsetDatetime.getOffset().getTotalSeconds() / 60 / 60;
        int hours = 9 - offsetHours;

        return datetime.minusHours(hours).toLocalDate();
    }

    /**
     * @param offsetDatetime 현지 offset datetime
     * @return 현지 시각 -> 한국 시각
     * @author Lee Taesung
     * @since 1.0
     */
    public static LocalDateTime convertLocalTimeToKoreanTime(OffsetDateTime offsetDatetime) {
        LocalDateTime datetime = offsetDatetime.toLocalDateTime();

        return applyUtcOffsetToKoreanTime(offsetDatetime, datetime);
    }
}

package com.benection.babymoment.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Slf4j
public class GsonUtils {
    private static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_TIME = "HH:mm:ss";
    private static final String PATTERN_DATETIME = String.format("%s %s", PATTERN_DATE, PATTERN_TIME);

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
//            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//            .setDateFormat(PATTERN_DATETIME)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter().nullSafe())
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter().nullSafe())
            .registerTypeAdapter(File.class, new FileAdapter().nullSafe())
            .setPrettyPrinting()
            .create();

    public static String toJson(Object o) {
        String result = gson.toJson(o);
        if ("null".equals(result)) {
            return null;
        }

        return result;
    }

    public static <T> T fromJson(String s, Class<T> clazz) {
        try {
            return gson.fromJson(s, clazz);
        } catch (JsonSyntaxException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_DATETIME);

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value != null)
                out.value(value.format(format));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), format);
        }
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_DATE);

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value.format(format));
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString(), format);
        }
    }

    static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_TIME);

        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            out.value(value.format(format));
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            return LocalTime.parse(in.nextString(), format);
        }
    }

    /**
     * offset datetime은 클라이언트로부터 2023-07-27T12:05:01+09:00를 받으면 2023-07-27T03:05:01Z 변경된다. 즉, UTC datetime으로 시각을 변환하고 포맷한 후 마지막에 Z를 붙여서 UTC 시간임을 표현한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    static class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {
        DateTimeFormatter format = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        @Override
        public void write(JsonWriter out, OffsetDateTime value) throws IOException {
            out.value(value.format(format));
        }

        @Override
        public OffsetDateTime read(JsonReader in) throws IOException {
            return OffsetDateTime.parse(in.nextString(), format);
        }
    }

    static class FileAdapter extends TypeAdapter<File> {
        @Override
        public void write(JsonWriter out, File value) throws IOException {
            out.value(value.getAbsolutePath());
        }

        @Override
        public File read(JsonReader in) throws IOException {
            return new File(in.nextString());
        }
    }
}

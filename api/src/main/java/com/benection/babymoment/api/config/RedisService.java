package com.benection.babymoment.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${project.name}")
    private String projectName; // Use key namespace for redis.

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public void setData(String key, String value, Long timeout) {
        redisTemplate.opsForValue().set(projectName + ":" + key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(projectName + ":" + key);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public void deleteData(String key) {
        redisTemplate.delete(projectName + ":" + key);
    }

    /**
     * 키를 통해 조회를 할 때 keys 명령어가 아닌 scan 명령어로 조회한다.
     *
     * @param keyPattern 검색어
     * @author Lee Taesung
     * @since 1.0
     */
    public List<String> scanData(String keyPattern) {
        ScanOptions options = ScanOptions.scanOptions().match(projectName + ":" + keyPattern).build();
        List<String> dataList = new ArrayList<>();
        try (Cursor<String> cursor = redisTemplate.scan(options)) { // Use RedisTemplate to scan directly.
            while (cursor.hasNext()) {
                String data = cursor.next().trim(); // Get the data and trim() it.
                String keyWithoutPrefix = data.replaceFirst("^" + projectName + ":", ""); // Remove the projectName prefix from the data.
                dataList.add(keyWithoutPrefix);
            }
        }

        return dataList;
    }
}

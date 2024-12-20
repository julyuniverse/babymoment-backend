package com.benection.babymoment.api.service;

import com.benection.babymoment.api.enums.AuthenticationLogType;
import com.benection.babymoment.api.entity.AuthenticationLog;
import com.benection.babymoment.api.repository.AuthenticationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static com.benection.babymoment.api.util.HttpHeaderUtils.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationLogService {
    private final AuthenticationLogRepository authenticationLogRepository;

    @Transactional
    public void createAuthenticationLog(AuthenticationLogType authenticationLogType, Integer deviceId, Integer accountId, Integer babyId) {
        String ipAddress = getIpAddress();
        String userAgent = getUserAgent();
        String appVersion = getAppVersion();
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        authenticationLogRepository.save(AuthenticationLog.builder()
                .type(authenticationLogType.name())
                .deviceId(deviceId)
                .accountId(accountId)
                .babyId(babyId)
                .ip(ipAddress)
                .userAgent(userAgent)
                .version(appVersion)
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build());
    }
}

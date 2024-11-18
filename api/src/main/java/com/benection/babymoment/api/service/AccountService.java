package com.benection.babymoment.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.benection.babymoment.api.config.RedisService;
import com.benection.babymoment.api.config.TokenProvider;
import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.AccountRequest;
import com.benection.babymoment.api.dto.AccountResponse;
import com.benection.babymoment.api.dto.StringResponse;
import com.benection.babymoment.api.dto.Status;
import com.benection.babymoment.api.enums.*;
import com.benection.babymoment.api.entity.*;
import com.benection.babymoment.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.benection.babymoment.api.util.ConvertUtils.convertAccountToAccountDto;
import static com.benection.babymoment.api.util.HttpHeaderUtils.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final RedisService redisService;
    private final AccountRepository accountRepository;
    private final DeviceRepository deviceRepository;
    private final RelationshipRepository relationshipRepository;
    private final RelationshipHistoryRepository relationshipHistoryRepository;
    private final BabyRepository babyRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationLogService authenticationLogService;
    private final PasswordEncoder passwordEncoder;

    public StringResponse string1() {
        return StringResponse.builder()
                .string("string1")
                .build();
    }

    /**
     * @return 계정
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<AccountResponse> getAccount(int accountId) {
        Account account = accountRepository.findByAccountId(accountId);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new AccountResponse(convertAccountToAccountDto(account)));
    }

    /**
     * @return 계정
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<AccountResponse> updateAccount(int accountId, AccountRequest request) {
        Account account = accountRepository.findByAccountId(accountId);
        if (StringUtils.hasText(request.getEmail()) && !Objects.equals(request.getEmail(), account.getEmail())) { // 이메일 변경
            if (accountRepository.existsByEmail(request.getEmail())) {
                return new ApiResponse<>(new Status(StatusCode.DUPLICATE_EMAIL), null);
            } else {
                account.updateEmail(request.getEmail());
            }
        }
        if (StringUtils.hasText(request.getPreviousPassword()) && StringUtils.hasText(request.getNewPassword())) {
            if (passwordEncoder.matches(request.getPreviousPassword(), account.getPassword())) { // 이전 비밀번호가 같다면
                account.updatePassword(passwordEncoder.encode(request.getNewPassword()));
            } else {
                return new ApiResponse<>(new Status(StatusCode.INVALID_PASSWORD), null);
            }
        }

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new AccountResponse(convertAccountToAccountDto(account)));
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> deleteAccount(int accountId) {
        // Get Device-Id header value, Datetime-Offset header value, Timezone-Identifier header value.
        int deviceId = Integer.parseInt(getDeviceId());
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        LocalDateTime now = LocalDateTime.now();
        Integer babyId = null;

        // 1. Get babyId.
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (Objects.equals(device.getAccountId(), accountId)) {
            if (device.getBabyId() != null) {
                babyId = device.getBabyId();
            }
        }

        // 2. Delete baby.
        // 2-1. 해당 account에 등록된 모든 baby 가져오기.
        List<Relationship> relationships = relationshipRepository.findByAccountId(accountId);

        // 3. relationship을 반복하면서 등록된 relationship 삭제, 권한이 admin일 경우 baby도 완전 삭제하기.
        for (Relationship relationship : relationships) {
            if (Authority.valueOf(relationship.getAuthority()) == Authority.ROLE_ADMIN) {
                // Delete baby.
                Baby baby = babyRepository.findByBabyId(relationship.getBabyId());
                baby.updateIsDeleted(true);
                baby.updateDeletedAt(now);

                // Update baby id of devices to null.
                List<Device> devices2 = deviceRepository.findByBabyId(baby.getBabyId());
                for (Device device2 : devices2) {
                    device2.updateBabyId(null);
                }

                // Create relationship.
                List<Relationship> relationships2 = relationshipRepository.findByBabyId(baby.getBabyId());
                for (Relationship relationship2 : relationships2) {
                    // Create relationship history.
                    relationshipHistoryRepository.save(RelationshipHistory.builder()
                            .type(RelationshipHistoryType.DELETE.name())
                            .accountId(relationship2.getAccountId())
                            .babyId(relationship2.getBabyId())
                            .relationshipType(RelationshipType.valueOf(relationship2.getType()).name())
                            .authority(Authority.valueOf(relationship2.getAuthority()).name())
                            .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                            .tzId(timezoneIdentifier)
                            .build());

                    // Delete relationship.
                    relationshipRepository.delete(relationship2);
                }
            } else {
                // Create relationship history.
                relationshipHistoryRepository.save(RelationshipHistory.builder()
                        .type(RelationshipHistoryType.DELETE.name())
                        .accountId(relationship.getAccountId())
                        .babyId(relationship.getBabyId())
                        .relationshipType(RelationshipType.valueOf(relationship.getType()).name())
                        .authority(Authority.valueOf(relationship.getAuthority()).name())
                        .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                        .tzId(timezoneIdentifier)
                        .build());

                // Delete relationship.
                relationshipRepository.delete(relationship);
            }
        }

        // 4. Delete account.
        Account account = accountRepository.findByAccountId(accountId);
        account.updateIsDeleted(true);
        account.updateDeletedAt(now);

        // 5. Update account id of devices to null.
        List<Device> devices2 = deviceRepository.findByAccountId(accountId);
        for (Device device2 : devices2) {
            device2.updateAccountId(null);
            device2.updateBabyId(null);
        }

        // 6. Delete token.
        // 6-1. Add access token to blacklist.
        DecodedJWT decodedJWT = tokenProvider.validateToken(getAuthorizationToken(), TokenType.ACCESS, false);
        long expiration = decodedJWT.getExpiresAt().getTime() - new Date().getTime();
        if (expiration > 0L) {
            redisService.setData(getAuthorizationToken(), "delete", expiration);
        }

        // 7. redis에서 accountId로 시작하는 refresh token들이 존재한다면 모두 삭제한다.
        List<String> refreshTokens = redisService.scanData(accountId + "*");
        for (String refreshToken : refreshTokens) {
            redisService.deleteData(refreshToken);
        }

        // Create authentication log.
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.DELETE, deviceId, accountId, babyId);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), null);
    }
}

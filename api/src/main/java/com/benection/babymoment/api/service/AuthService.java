package com.benection.babymoment.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.aws.ses.service.EmailService;
import com.benection.babymoment.api.config.CustomException;
import com.benection.babymoment.api.config.ErrorCode;
import com.benection.babymoment.api.config.RedisService;
import com.benection.babymoment.api.config.TokenProvider;
import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.account.PasswordRecoveryRequest;
import com.benection.babymoment.api.dto.auth.*;
import com.benection.babymoment.api.dto.Status;
import com.benection.babymoment.api.dto.auth.TokenReissueRequest;
import com.benection.babymoment.api.dto.auth.TokenDto;
import com.benection.babymoment.api.enums.AuthenticationLogType;
import com.benection.babymoment.api.enums.StatusCode;
import com.benection.babymoment.api.enums.TokenType;
import com.benection.babymoment.api.entity.Account;
import com.benection.babymoment.api.entity.Baby;
import com.benection.babymoment.api.entity.Device;
import com.benection.babymoment.api.entity.Relationship;
import com.benection.babymoment.api.repository.AccountRepository;
import com.benection.babymoment.api.repository.BabyRepository;
import com.benection.babymoment.api.repository.DeviceRepository;
import com.benection.babymoment.api.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.benection.babymoment.api.util.ConvertUtils.convertAccountToAccountDto;
import static com.benection.babymoment.api.util.ConvertUtils.convertBabyToBabyDto;
import static com.benection.babymoment.api.util.HttpHeaderUtils.*;
import static com.benection.babymoment.api.util.RandomUtils.generateRandomString;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final DeviceRepository deviceRepository;
    private final AccountRepository accountRepository;
    private final RelationshipRepository relationshipRepository;
    private final BabyRepository babyRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final AuthenticationLogService authenticationLogService;
    private final EmailService emailService;
    @Value("${token.ttl.refresh-token}")
    private Long refreshTokenTtl;

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<UuidLoginResponse> loginWithUuid(UuidLoginRequest request) {
        UuidLoginResponse uuidLoginResponse = new UuidLoginResponse();

        // Check if device exists by uuid and if not create it.
        Device device = deviceRepository.findByUuid(request.getDeviceUuid()).orElseGet(() -> Device.builder().uuid(request.getDeviceUuid()).build());
        device.updateModel(request.getDeviceModel());
        device.updateSystemName(request.getSystemName());
        device.updateSystemVersion(request.getSystemVersion());

        // Check device.
        if (device.getAccountId() != null) { // accountId가 등록되어 있다면
            Optional<Account> optionalAccount = accountRepository.findByAccountIdAndIsDeletedFalse(device.getAccountId());
            if (optionalAccount.isPresent()) { // account가 있다면
                Account account = optionalAccount.get();
                Baby baby = null;
                Relationship relationship = null;
                Optional<Relationship> optionalRelationship = relationshipRepository.findTopByAccountIdOrderByCreatedAt(account.getAccountId());
                if (optionalRelationship.isPresent()) {
                    Optional<Baby> optionalBaby = babyRepository.findByBabyIdAndIsDeleted(optionalRelationship.get().getBabyId(), false);
                    if (optionalBaby.isPresent()) {
                        baby = optionalBaby.get();
                        relationship = optionalRelationship.get();
                    }
                }
                if (device.getBabyId() != null) {
                    Optional<Relationship> optionalRelationship2 = relationshipRepository.findByAccountIdAndBabyId(device.getAccountId(), device.getBabyId());
                    if (optionalRelationship2.isPresent()) {
                        Optional<Baby> optionalBaby = babyRepository.findByBabyIdAndIsDeleted(device.getBabyId(), false);
                        if (optionalBaby.isPresent()) {
                            baby = optionalBaby.get();
                            relationship = optionalRelationship2.get();
                        }
                    }
                }
                if (baby != null) {
                    uuidLoginResponse.setBaby(convertBabyToBabyDto(baby, relationship));
                    device.updateBabyId(baby.getBabyId());
                } else {
                    device.updateBabyId(null);
                }

                // Set return value.
                uuidLoginResponse.setAccount(convertAccountToAccountDto(account));
            } else {
                device.updateAccountId(null);
                device.updateBabyId(null);
            }
        } else {
            device.updateBabyId(null);
        }
        deviceRepository.save(device);
        uuidLoginResponse.setDeviceId(device.getDeviceId());

        // Create authentication log.
        authenticationLogService.save(AuthenticationLogType.UUID_LOGIN, device.getDeviceId(), device.getAccountId(), device.getBabyId());

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), uuidLoginResponse);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> signupWithEmail(EmailSignupRequest emailSignupRequest) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();

        // Get Device-Id header value, Datetime-Offset header value, Timezone-Identifier header value.
        Integer deviceId = Integer.valueOf(getDeviceId());
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();

        // id(email) 중복을 확인한다.
        if (accountRepository.existsByEmail(emailSignupRequest.getEmail())) {
            apiResponse.setStatus(new Status(StatusCode.DUPLICATE_EMAIL));

            return apiResponse;
        }
        Account account = Account.builder()
                .email(emailSignupRequest.getEmail())
                .password(passwordEncoder.encode(emailSignupRequest.getPassword()))
                .username(emailSignupRequest.getUsername())
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build();
        accountRepository.save(account);

        // Create authentication log.
        authenticationLogService.save(AuthenticationLogType.SIGNUP, deviceId, account.getAccountId(), null);

        // Set return value.
        apiResponse.setStatus(new Status(StatusCode.SUCCESS));

        return apiResponse;
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<EmailLoginResponse> loginWithEmail(EmailLoginRequest request) {
        ApiResponse<EmailLoginResponse> apiResponse = new ApiResponse<>();

        // Get Device-Id header value.
        Integer deviceId = Integer.valueOf(getDeviceId());

        // Get account.
        Account account;
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isPresent()) {
            if (optionalAccount.get().getIsDeleted()) { // 삭제된 상태라면
                apiResponse.setStatus(new Status(StatusCode.NOT_FOUND_ACCOUNT));

                return apiResponse;
            } else if (!passwordEncoder.matches(request.getPassword(), optionalAccount.get().getPassword())) { // 비밀번호가 틀리다면
                apiResponse.setStatus(new Status(StatusCode.INVALID_PASSWORD));

                return apiResponse;
            }
            account = optionalAccount.get();
        } else {
            apiResponse.setStatus(new Status(StatusCode.NOT_FOUND_ACCOUNT));

            return apiResponse;
        }

        // Get baby.
        Baby baby = null;
        Integer babyId = null;
        Relationship relationship = null;
        Optional<Relationship> relationshipOptional = relationshipRepository.findTopByAccountIdOrderByCreatedAt(account.getAccountId());
        if (relationshipOptional.isPresent()) {
            Optional<Baby> babyOptional = babyRepository.findByBabyIdAndIsDeleted(relationshipOptional.get().getBabyId(), false);
            if (babyOptional.isPresent()) {
                baby = babyOptional.get();
                relationship = relationshipOptional.get();
            }
        }

        // Get device.
        Device device = deviceRepository.findByDeviceId(deviceId);
        device.updateAccountId(account.getAccountId());
        device.updateBabyId(baby != null ? baby.getBabyId() : null);

        // Issue token.
        // Create access token, refresh token.
        // 1. Create UsernamePasswordAuthenticationToken object based on accountId and password.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(Long.toString(account.getAccountId()), request.getPassword());

        // 2. Validate user.
        // authenticationManagerBuilder.getObject().authenticate() 메서드가 실행될 때 CustomUserDetailsService에서 만들었던 loadUserByUsername() 메서드가 실행됨 -> 사전에 위에서 설정한 UsernamePasswordAuthenticationToken가 반드시 적용되어 있어야 한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. Create token based on authentication information.
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(tokenProvider.createAccessToken(authentication, String.valueOf(deviceId)))
                .refreshToken(tokenProvider.createRefreshToken(authentication, String.valueOf(deviceId)))
                .build();

        // 4. Insert refresh token into redis. (삽입 시 키 이름은 accountId:deviceId로 설정한다.)
        redisService.setData(account.getAccountId() + ":" + device.getDeviceId(), tokenDto.getRefreshToken(), refreshTokenTtl);

        // Set return value.
        EmailLoginResponse emailLoginResponse = EmailLoginResponse.builder()
                .token(tokenDto)
                .account(convertAccountToAccountDto(account))
                .build();
        if (baby != null) {
            emailLoginResponse.setBaby(convertBabyToBabyDto(baby, relationship));
            babyId = baby.getBabyId();
        }

        // Create authentication log.
        authenticationLogService.save(AuthenticationLogType.EMAIL_LOGIN, device.getDeviceId(), account.getAccountId(), babyId);

        // Set return value.
        apiResponse.setStatus(new Status(StatusCode.SUCCESS));
        apiResponse.setData(emailLoginResponse);

        return apiResponse;
    }

    /**
     * 토큰 예외 발생 시 해당 device에 등록된 accountId, babyId를 null 처리하고 강제 로그아웃 로그를 생성한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public void setNullToDevice(int deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        authenticationLogService.save(AuthenticationLogType.FORCE_LOGOUT, deviceId, device.getAccountId(), device.getBabyId());
        device.updateAccountId(null);
        device.updateBabyId(null);
    }

    /**
     * 토큰을 재발행한다.
     *
     * @return 토큰
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(noRollbackFor = {CustomException.class})
    public ApiResponse<TokenReissueResponse> reissueToken(TokenReissueRequest request) {
        // Get Device-Id header value.
        Integer deviceId = Integer.valueOf(getDeviceId());

        // 1. Validate token.
        Authentication authentication;
        try {
            // 2. Validate refresh token.
            DecodedJWT decodedJWT = tokenProvider.validateToken(request.getRefreshToken(), TokenType.REFRESH, true);

            // 3. Get account id from access token.
            authentication = tokenProvider.getAuthentication(getAuthorizationToken(), TokenType.ACCESS);

            // 4. Check if sub of access token matches sub of refresh token.
            if (!Objects.equals(decodedJWT.getSubject(), authentication.getName())) {
                throw new CustomException(ErrorCode.MISMATCH_TOKEN);
            }
        } catch (CustomException e) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] ErrorCode: " + e.getErrorCode().name());
            throw new CustomException(e.getErrorCode());
        }

        // 4. Get refresh token generated based on (accountId:deviceId) from redis.
        String refreshToken = redisService.getData(authentication.getName() + ":" + deviceId);

        // 5. Check if refresh token exists.
        if (refreshToken == null) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] 로그아웃된 계정이에요.");
            throw new CustomException(ErrorCode.LOGGED_OUT_ACCOUNT);
        }

        // 6. Check if refresh token matches each other.
        if (!Objects.equals(refreshToken, request.getRefreshToken())) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] 기존 토큰와 일치하지 않아요.");
            throw new CustomException(ErrorCode.MISMATCH_TOKEN);
        }

        // 7. Create new refresh token.
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(tokenProvider.createAccessToken(authentication, String.valueOf(deviceId)))
                .refreshToken(tokenProvider.createRefreshToken(authentication, String.valueOf(deviceId)))
                .build();

        // 8. Update refresh token in redis.
        redisService.setData(authentication.getName() + ":" + deviceId, tokenDto.getRefreshToken(), refreshTokenTtl);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new TokenReissueResponse(tokenDto));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(noRollbackFor = {CustomException.class})
    public ApiResponse<Void> logout() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();

        // Get Device-Id header value.
        Integer deviceId = Integer.valueOf(getDeviceId());
        Integer accountId = null;
        Integer babyId = null;

        // 1-1. Validate access token.
        Authentication authentication;
        try {
            // 1-2. Get account id from access token.
            authentication = tokenProvider.getAuthentication(getAuthorizationToken(), TokenType.ACCESS);
            accountId = Integer.valueOf(authentication.getName());

            // 2. Get device.
            Device device = deviceRepository.findByDeviceId(deviceId);

            // 3. If there is refresh token generated based on (accountId:deviceId) in redis, delete it.
            if (redisService.getData(accountId + ":" + device.getDeviceId()) != null) {
                redisService.deleteData(accountId + ":" + device.getDeviceId());

                // 3. Add access token to blacklist.
                DecodedJWT decodedJWT = tokenProvider.validateToken(getAuthorizationToken(), TokenType.ACCESS, false);
                long expiration = decodedJWT.getExpiresAt().getTime() - new Date().getTime();
                if (expiration > 0L) {
                    redisService.setData(getAuthorizationToken(), "logout", expiration);
                }
            }

            // 4. Get babyId.
            if (device.getBabyId() != null) {
                babyId = device.getBabyId();
            }

            // 5. Set device account id and device baby id to null.
            device.updateAccountId(null);
            device.updateBabyId(null);

            // 6. Create authentication log.
            authenticationLogService.save(AuthenticationLogType.LOGOUT, deviceId, accountId, babyId);

            // 7. Set return value.
            apiResponse.setStatus(new Status(StatusCode.SUCCESS));

            return apiResponse;
        } catch (CustomException e) {
            authenticationLogService.save(AuthenticationLogType.LOGOUT, deviceId, accountId, babyId);
            setNullToDevice(deviceId);
            log.info("[logout] ErrorCode: " + e.getErrorCode().name());

            // Set return value.
            apiResponse.setStatus(new Status(StatusCode.SUCCESS));

            return apiResponse;
        }
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> processPasswordRecovery(PasswordRecoveryRequest request) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String randomString = generateRandomString(8);
            account.updatePassword(passwordEncoder.encode(randomString));
            emailService.sendEmail(account.getEmail(), "임시 비밀번호", "임시 비밀번호: " + randomString);

            // Set return value.
            apiResponse.setStatus(new Status(StatusCode.SUCCESS));

            return apiResponse;
        } else {
            // Set return value.
            apiResponse.setStatus(new Status(StatusCode.NOT_FOUND_ACCOUNT));

            return apiResponse;
        }
    }
}

package com.benection.babymoment.api.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.benection.babymoment.api.config.*;
import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.LoginResponse;
import com.benection.babymoment.api.dto.SocialLoginRequest;
import com.benection.babymoment.api.dto.PasswordRecoveryRequest;
import com.benection.babymoment.api.dto.auth.*;
import com.benection.babymoment.api.dto.StatusDto;
import com.benection.babymoment.api.dto.auth.TokenReissueRequest;
import com.benection.babymoment.api.dto.auth.TokenDto;
import com.benection.babymoment.api.enums.AuthenticationLogType;
import com.benection.babymoment.api.enums.Authority;
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
import com.benection.babymoment.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.*;

import static com.benection.babymoment.api.util.ConvertUtils.convertAccountToAccountDto;
import static com.benection.babymoment.api.util.ConvertUtils.convertBabyToBabyDto;
import static com.benection.babymoment.api.util.HttpHeaderUtils.*;
import static com.benection.babymoment.api.util.RandomUtils.generateRandomString;
import static com.benection.babymoment.api.util.RedisKeyUtils.generateRefreshTokenKey;

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
    @Value("${apple.bundle-id}")
    private String appleBundleId;

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<UuidLoginResponse> loginWithUuid(UuidLoginRequest request) {
        UuidLoginResponse uuidLoginResponse = new UuidLoginResponse();

        // Check if device exists by uuid and if not create it.
        Device device = deviceRepository.findByUuid(request.getDeviceUuid())
                .map(v -> {
                    v.updateModel(request.getDeviceModel());
                    v.updateSystemName(request.getSystemName());
                    v.updateSystemVersion(request.getSystemVersion());

                    return v;
                })
                .orElseGet(() -> deviceRepository.save(Device.builder()
                        .uuid(request.getDeviceUuid())
                        .model(request.getDeviceModel())
                        .systemName(request.getSystemName())
                        .systemVersion(request.getSystemVersion())
                        .build()));

        // Check device.
        if (device.getAccountId() != null) { // accountId가 등록되어 있다면
            Optional<Account> optionalAccount = accountRepository.findByAccountIdAndIsDeletedFalse(device.getAccountId());
            if (optionalAccount.isPresent()) { // account가 있다면
                Account account = optionalAccount.get();
                Baby baby = null;
                Relationship relationship = null;
                Optional<Relationship> optionalRelationship = relationshipRepository.findTopByAccountIdOrderByCreatedAt(account.getAccountId());
                if (optionalRelationship.isPresent()) {
                    Optional<Baby> optionalBaby = babyRepository.findByBabyIdAndIsDeletedFalse(optionalRelationship.get().getBabyId());
                    if (optionalBaby.isPresent()) {
                        baby = optionalBaby.get();
                        relationship = optionalRelationship.get();
                    }
                }
                if (device.getBabyId() != null) {
                    Optional<Relationship> optionalRelationship2 = relationshipRepository.findByAccountIdAndBabyId(device.getAccountId(), device.getBabyId());
                    if (optionalRelationship2.isPresent()) {
                        Optional<Baby> optionalBaby = babyRepository.findByBabyIdAndIsDeletedFalse(device.getBabyId());
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
        uuidLoginResponse.setDeviceId(device.getDeviceId());

        // Create authentication log.
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.UUID_LOGIN, device.getDeviceId(), device.getAccountId(), device.getBabyId());

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), uuidLoginResponse);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> signupWithEmail(EmailSignupRequest emailSignupRequest) {
        // Get Device-Id header value, Datetime-Offset header value, Timezone-Identifier header value.
        Integer deviceId = Integer.valueOf(getDeviceId());
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();

        // id(email) 중복을 확인한다.
        if (accountRepository.existsByEmail(emailSignupRequest.getEmail())) {
            return new ApiResponse<>(new StatusDto(StatusCode.DUPLICATE_EMAIL), null);
        }
        Account account = accountRepository.save(Account.builder()
                .email(emailSignupRequest.getEmail())
                .password(passwordEncoder.encode(emailSignupRequest.getPassword()))
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build());

        // Create authentication log.
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.SIGNUP, deviceId, account.getAccountId(), null);

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), null);
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<EmailLoginResponse> loginWithEmail(EmailLoginRequest request) {
        // Get Device-Id header value.
        Integer deviceId = Integer.valueOf(getDeviceId());

        // Get account.
        Account account;
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isPresent()) {
            if (optionalAccount.get().getIsDeleted()) { // 삭제된 상태라면
                return new ApiResponse<>(new StatusDto(StatusCode.NOT_FOUND_ACCOUNT), null);
            } else if (!passwordEncoder.matches(request.getPassword(), optionalAccount.get().getPassword())) { // 비밀번호가 틀리다면
                return new ApiResponse<>(new StatusDto(StatusCode.INVALID_PASSWORD), null);
            }
            account = optionalAccount.get();
        } else {
            return new ApiResponse<>(new StatusDto(StatusCode.NOT_FOUND_ACCOUNT), null);
        }

        // Get baby.
        Baby baby = null;
        Integer babyId = null;
        Relationship relationship = null;
        Optional<Relationship> relationshipOptional = relationshipRepository.findTopByAccountIdOrderByCreatedAt(account.getAccountId());
        if (relationshipOptional.isPresent()) {
            Optional<Baby> babyOptional = babyRepository.findByBabyIdAndIsDeletedFalse(relationshipOptional.get().getBabyId());
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

        // 4. Insert refresh token into redis. (삽입 시 키 이름은 refresh_token:accountId_deviceId로 설정한다.)
        redisService.setData("refresh_token:" + account.getAccountId() + "_" + device.getDeviceId(), tokenDto.getRefreshToken(), refreshTokenTtl);

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
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.EMAIL_LOGIN, device.getDeviceId(), account.getAccountId(), babyId);

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), emailLoginResponse);
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
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.FORCE_LOGOUT, deviceId, device.getAccountId(), device.getBabyId());
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
        redisService.setData("refresh_token:" + authentication.getName() + "_" + deviceId, tokenDto.getRefreshToken(), refreshTokenTtl);

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), new TokenReissueResponse(tokenDto));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(noRollbackFor = {CustomException.class})
    public ApiResponse<Void> logout() {
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
            authenticationLogService.createAuthenticationLog(AuthenticationLogType.LOGOUT, deviceId, accountId, babyId);

            return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), null);
        } catch (CustomException e) {
            authenticationLogService.createAuthenticationLog(AuthenticationLogType.LOGOUT, deviceId, accountId, babyId);
            setNullToDevice(deviceId);
            log.info("[logout] ErrorCode: " + e.getErrorCode().name());

            return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), null);
        }
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> processPasswordRecovery(PasswordRecoveryRequest request) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String randomString = generateRandomString(8);
            account.updatePassword(passwordEncoder.encode(randomString));
            emailService.sendEmail(account.getEmail(), "임시 비밀번호", "임시 비밀번호: " + randomString);

            return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), null);
        } else {
            return new ApiResponse<>(new StatusDto(StatusCode.NOT_FOUND_ACCOUNT), null);
        }
    }

    /**
     * 소셜 제공자를 통해 로그인한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<LoginResponse> loginWithSocialProvider(SocialLoginRequest request) throws JwkException, MalformedURLException, URISyntaxException {
        // Get Device-Id header value, Datetime-Offset header value, Timezone-Identifier header value.
        String deviceId = getDeviceId();
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();

        // idToken 검증 5가지
        // apple 개발자 문서: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user#3383769
        // Verify the identity token ↓
        // 1. Verify the JWS E256 signature using the server’s public key
        // 2. Verify the nonce for the authentication ↓
        // nonce는 클라이언트에서 생성한 값으로, 보안 상의 이유로 재전송 공격을 방지한다.
        // 클라이언트에서 생성한 nonce를 identityToken을 검증할 때 포함시켜야 하지만, 클라이언트에서 identityToken을 얻기 때문에 서버에서는 이 값을 검증할 수 없다.
        // 보통 이 검증은 클라이언트에서 이루어진다.
        // 3. Verify that the iss field contains https://appleid.apple.com
        // 4. Verify that the aud field is the developer’s client_id
        // 5. Verify that the time is earlier than the exp value of the token

        // 1. server’s public key를 사용한 검증(1번 검증)
        // apple 서버로부터 공개키 3개 가져오기.

        JwkProvider provider = new UrlJwkProvider(new URI("https://appleid.apple.com/auth/keys").toURL());
        DecodedJWT jwt = JWT.decode(request.getIdToken());
        Jwk jwk = provider.get(jwt.getKeyId());

        // 2. Validate token.
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("https://appleid.apple.com")
                .build();
        DecodedJWT decodedJWT = verifier.verify(request.getIdToken());

        // 3. Validate audience.
        if (!decodedJWT.getAudience().contains(appleBundleId)) {
            throw new RuntimeException("Invalid audience.");
        }

        // 4. Validate expiration.
        if (decodedJWT.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Token has expired.");
        }

        // Extract user info.
        String userIdentifier = decodedJWT.getSubject();
        String email = decodedJWT.getClaim("email").asString();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();

        // Insert db data or update.
        Account account = accountRepository.findByUserIdentifier(userIdentifier)
                .map(v -> {
                    v.updateEmail(email);
                    if (StringUtils.hasText(firstName)) v.updateFirstName(firstName);
                    if (StringUtils.hasText(lastName)) v.updateLastName(lastName);

                    return v;
                })
                .orElseGet(() -> accountRepository.save(Account.builder()
                        .userIdentifier(userIdentifier)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .authority(Authority.ROLE_USER.name())
                        .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                        .tzId(timezoneIdentifier)
                        .build()));

        // Update device.
        Device device = deviceRepository.findByDeviceId(Integer.parseInt(deviceId));
        device.updateAccountId(account.getAccountId());

        // Issue tokens.
        // Create access token, refresh token.
        // 1. 소셜 인증 토큰 생성한다.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(account.getAuthority()));
        SocialAuthenticationToken authenticationToken = new SocialAuthenticationToken(account.getAccountId(), authorities);

        // 2. SecurityContext에 인증 정보 설정한다.
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 3. 토큰을 발급한다.
        TokenDto tokenDto = new TokenDto(tokenProvider.createAccessToken(authenticationToken, deviceId), tokenProvider.createRefreshToken(authenticationToken, deviceId));

        // 4. redis에 refresh token 생성한다. (생성 시 키 이름은 refresh_token:accountId_deviceId로 설정한다.)
        redisService.setData(generateRefreshTokenKey(account.getAccountId() + "_" + device.getDeviceId()), tokenDto.getRefreshToken(), refreshTokenTtl);

        // 5. Set return value.
        LoginResponse loginResponse = new LoginResponse(convertAccountToAccountDto(account), tokenDto);

        // 6. Create authentication log.
        authenticationLogService.createAuthenticationLog(AuthenticationLogType.SOCIAL_LOGIN, Integer.parseInt(deviceId), account.getAccountId(), null);

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), loginResponse);
    }
}

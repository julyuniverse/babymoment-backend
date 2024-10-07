package com.benection.babymoment.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAILURE(HttpStatus.BAD_REQUEST, "Failure", "Failure occurred."),
    INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServer", "Internal server error."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "ExpiredToken", "Token has expired."),
    INVALID_SIGNATURE_TOKEN(HttpStatus.UNAUTHORIZED, "InvalidSignatureToken", "Invalid token signature."),
    DECODING_FAILED_TOKEN(HttpStatus.BAD_REQUEST, "DecodingFailedToken", "Failed to decode token."),
    VERIFICATION_FAILED_TOKEN(HttpStatus.UNAUTHORIZED, "VerificationFailedToken", "Token verification failed."),
    MISSING_AUTHORITIES_CLAIM(HttpStatus.BAD_REQUEST, "MissingAuthoritiesClaim", "Token is missing authorities claim."),
    NOT_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "NotAccessToken", "Not an access token."),
    NOT_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "NotRefreshToken", "Not a refresh token."),
    MISSING_TOKEN_TYPE_CLAIM(HttpStatus.BAD_REQUEST, "MissingTokenTypeClaim", "Token is missing token_type claim."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "MissingToken", "Token is missing."),
    MISMATCH_TOKEN(HttpStatus.UNAUTHORIZED, "MismatchToken", "Token mismatch."),
    MISSING_DEVICE_ID(HttpStatus.BAD_REQUEST, "MissingDeviceId", "Device ID is missing."),
    LOGGED_OUT_ACCOUNT(HttpStatus.UNAUTHORIZED, "LoggedOutAccount", "Account has been logged out."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "InvalidCredentials", "Credentials are invalid."),
    MISSING_ACCEPT_LANGUAGE(HttpStatus.BAD_REQUEST, "MissingAcceptLanguage", "Accept language is missing."),
    MISSING_OFFSET_DATETIME(HttpStatus.BAD_REQUEST, "MissingOffsetDatetime", "Offset Datetime is missing."),
    INVALID_FORMAT_OFFSET_DATETIME(HttpStatus.BAD_REQUEST, "InvalidFormatOffsetDatetime", "Invalid Offset Datetime format."),
    INVALID_OFFSET_DATETIME_RANGE(HttpStatus.BAD_REQUEST, "InvalidOffsetDatetimeRange", "Offset Datetime range is invalid."),
    MISSING_TIMEZONE_IDENTIFIER(HttpStatus.BAD_REQUEST, "MissingTimezoneIdentifier", "Timezone Identifier is missing."),
    MISSING_APP_VERSION(HttpStatus.BAD_REQUEST, "MissingAppVersion", "App Version is missing."),
    MISSING_PLATFORM(HttpStatus.BAD_REQUEST, "MissingPlatform", "Platform is missing."),
    UPDATE_REQUIRED_APP_VERSION(HttpStatus.UPGRADE_REQUIRED, "UpdateRequiredAppVersion", "App version update is required."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

package com.benection.babymoment.api.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Builder
public class ErrorResponse {
    private final OffsetDateTime timestamp = OffsetDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final String code;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().getReasonPhrase())
                        .message(errorCode.getMessage())
                        .code(errorCode.getCode())
                        .build()
                );
    }

    public static ResponseEntity<?> toResponseEntity(Exception e, HttpStatus httpStatus, String code) {
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
                        .status(httpStatus.value())
                        .error(httpStatus.getReasonPhrase())
                        .message(e.getMessage())
                        .code(code)
                        .build()
                );
    }
}

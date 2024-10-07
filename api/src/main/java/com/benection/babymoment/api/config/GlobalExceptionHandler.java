package com.benection.babymoment.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.benection.babymoment.api.config.ErrorCode.INTERNAL_SERVER;
import static com.benection.babymoment.api.util.HttpHeaderUtils.getDeviceId;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestControllerAdvice // view를 구현하지 않고 rest api로만 개발할 때는 @RestControllerAdvice를 사용한다.
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[handleCustomException] deviceId: {}", getDeviceId());
        log.error("[handleCustomException] Exception StackTrace: {", e);
        log.error("}");
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<?> handleException(Exception e) {
        log.error("[handleException] deviceId: {}", getDeviceId());
        log.error("[handleException] Exception StackTrace: {", e);
        log.error("}");
        return ErrorResponse.toResponseEntity(e, INTERNAL_SERVER.getHttpStatus(), INTERNAL_SERVER.getCode());
    }
}

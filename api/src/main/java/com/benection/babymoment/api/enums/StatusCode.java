package com.benection.babymoment.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@AllArgsConstructor
@Schema(description = "상태 코드")
public enum StatusCode {
    FAILURE("Failure", "Operation failed."),
    SUCCESS("Success", "Operation succeeded."),
    NOT_FOUND_ACCOUNT("NotFoundAccount", "Account could not be found."),
    DUPLICATE_EMAIL("DuplicateEmail", "Email already exists."),
    INVALID_PASSWORD("InvalidPassword", "Invalid password."),
    EXCEEDED_LIMIT_CREATION("ExceededLimitCreation", "Creation limit exceeded."),
    NOT_FOUND_CODE("NotFoundCode", "Code could not be found."),
    ALREADY_DELETED_BABY("AlreadyDeletedBaby", "Baby already deleted."),
    ALREADY_REGISTERED_BABY("AlreadyRegisteredBaby", "Baby already registered."),
    UNSUPPORTED_EXTENSION("UnsupportedExtension", "Unsupported extension."),
    NOT_FOUND_BABY("NotFoundBaby", "Baby could not be found."),
    ;

    private final String code;
    private final String message;
}

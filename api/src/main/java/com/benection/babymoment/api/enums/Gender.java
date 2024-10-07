package com.benection.babymoment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum Gender {
    WOMAN("woman"),
    MAN("man"),
    ;

    private final String name;
}

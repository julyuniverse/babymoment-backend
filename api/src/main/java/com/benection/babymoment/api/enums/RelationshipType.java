package com.benection.babymoment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum RelationshipType {
    MOM("mom"),
    DAD("dad"),
    BABYSITTER("babysitter"),
    ETC("etc"),
    ;

    private final String name;
}

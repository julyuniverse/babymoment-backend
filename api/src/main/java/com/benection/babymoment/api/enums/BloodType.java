package com.benection.babymoment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum BloodType {
    A_RH_PLUS("A Rh+"),
    A_RH_MINUS("A Rh-"),
    B_RH_PLUS("B Rh+"),
    B_RH_MINUS("B Rh-"),
    AB_RH_PLUS("AB Rh+"),
    AB_RH_MINUS("AB Rh-"),
    O_RH_PLUS("O Rh+"),
    O_RH_MINUS("O Rh-"),
    ;

    public final String name;
}

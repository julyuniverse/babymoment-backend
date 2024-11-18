package com.benection.babymoment.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    @Schema(description = "사용자 아이덴티티 토큰")
    private String idToken;
    @Schema(description = "소셜 로그인 제공자(apple, google)")
    private String provider;
    private String firstName;
    private String lastName;
}

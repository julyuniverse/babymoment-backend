package com.benection.babymoment.api.dto.auth;

import lombok.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenReissueRequest {
    private String refreshToken;
}

package com.benection.babymoment.api.dto.auth;

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
public class UuidLoginRequest {
    private String deviceUuid;
    private String deviceModel;
    private String systemName;
    private String systemVersion;
}

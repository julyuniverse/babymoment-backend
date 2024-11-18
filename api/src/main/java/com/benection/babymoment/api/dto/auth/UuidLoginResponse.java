package com.benection.babymoment.api.dto.auth;

import com.benection.babymoment.api.dto.AccountDTO;
import com.benection.babymoment.api.dto.baby.BabyDto;
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
public class UuidLoginResponse {
    private Integer deviceId;
    private AccountDTO account;
    private BabyDto baby;
}

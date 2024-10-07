package com.benection.babymoment.api.dto.auth;

import com.benection.babymoment.api.dto.account.AccountDto;
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
public class EmailLoginResponse {
    private TokenDto token;
    private AccountDto account;
    private BabyDto baby;
}

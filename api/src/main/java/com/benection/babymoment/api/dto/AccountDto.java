package com.benection.babymoment.api.dto;

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
public class AccountDto {
    private Integer accountId;
    private String email;
    private String firstName;
    private String lastName;
}

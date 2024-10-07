package com.benection.babymoment.api.dto.baby;

import lombok.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeGenerationResponse {
    private String code;
    private Long seconds;
}

package com.benection.babymoment.api.dto;

import com.benection.babymoment.api.enums.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
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
@Builder
public class StatusDto {
    @Schema(description = "코드")
    private String code;
    @Schema(description = "메시지")
    private String message;

    public StatusDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public StatusDto(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
}

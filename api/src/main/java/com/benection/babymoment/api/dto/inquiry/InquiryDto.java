package com.benection.babymoment.api.dto.inquiry;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDto {
    private Integer inquiryId;
    private String title;
    private String content;
    private LocalDateTime inquiredAt;
    private String answer;
    private LocalDateTime answeredAt;
}

package com.benection.babymoment.api.dto.inquiry;

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
public class InquiryRequest {
    private Integer accountId;
    private Integer babyId;
    private String title;
    private String content;
}

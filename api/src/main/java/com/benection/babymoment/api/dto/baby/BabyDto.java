package com.benection.babymoment.api.dto.baby;

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
public class BabyDto {
    @Builder.Default
    private Integer babyId = 0;
    @Builder.Default
    private String name = "";
    @Builder.Default
    private LocalDateTime birthday = LocalDateTime.now();
    @Builder.Default
    private String gender = "";
    @Builder.Default
    private String bloodType = "";
    @Builder.Default
    private String relationshipType = "";
    @Builder.Default
    private String imageFileName = "";
    @Builder.Default
    private String authority = "";
}

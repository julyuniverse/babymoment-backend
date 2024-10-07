package com.benection.babymoment.api.dto.pattern;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String type;
}

package com.benection.babymoment.api.dto.pattern;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPatternDto {
    private LocalDate date;
    private Long dDay;
    private List<PatternDto> patterns;
}

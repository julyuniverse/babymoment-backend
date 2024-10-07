package com.benection.babymoment.api.dto.activity;

import lombok.*;

import java.time.LocalDate;
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
public class ActivityDto {
    private Integer activityId;
    private Integer babyId;
    private LocalDate localDate;
    private String type;
    private String type2;
    private String memo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Float rightBreast;
    private Float leftBreast;
    private Float breast;
    private Float rightAmount;
    private Float leftAmount;
    private Float amount;
    private String unit;
    private String unit2;
}

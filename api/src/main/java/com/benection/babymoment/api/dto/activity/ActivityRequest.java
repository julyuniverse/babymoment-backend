package com.benection.babymoment.api.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {
    private Integer babyId;
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

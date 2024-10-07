package com.benection.babymoment.api.dto.activity;

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
public class ActivityWrapper {
    private LocalDate localDate;
    private Long dDay;
    private Long powderedMilkAmount;
    private Long babyFoodAmount;
    private Long milkAmount;
    private Long snackAmount;
    private List<ActivityDto> activities;
}

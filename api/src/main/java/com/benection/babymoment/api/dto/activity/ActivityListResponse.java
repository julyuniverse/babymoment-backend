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
public class ActivityListResponse {
    private List<ActivityWrapper> activityWrappers;
    private LocalDate lastDate;
}

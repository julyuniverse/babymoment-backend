package com.benection.babymoment.api.service;

import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.activity.*;
import com.benection.babymoment.api.dto.Status;
import com.benection.babymoment.api.enums.StatusCode;
import com.benection.babymoment.api.entity.Activity;
import com.benection.babymoment.api.entity.Baby;
import com.benection.babymoment.api.repository.ActivityRepository;
import com.benection.babymoment.api.repository.BabyRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.benection.babymoment.api.util.ConvertUtils.convertActivityToActivityDto;
import static com.benection.babymoment.api.util.DateUtils.*;
import static com.benection.babymoment.api.util.HttpHeaderUtils.getDatetimeOffset;
import static com.benection.babymoment.api.util.HttpHeaderUtils.getTimezoneIdentifier;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final BabyRepository babyRepository;

    /**
     * @return 활동
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<ActivityResponse> createActivity(ActivityRequest request) {
        // Get Datetime-Offset header value, Timezone-Identifier header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();

        // 시작 시각과 종료 시각의 차이가 최대 23시간 59분까지 가능하다.
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        if (endTime == null) {
            startTime = applyUtcOffsetToKoreanTime(datetimeOffset, startTime);
        } else {
            // 최대 23시간 59분 차이까지 계산한다.
            Duration maxDuration = Duration.ofHours(23).plusMinutes(59);
            Duration durationBetween = Duration.between(startTime, endTime);

            // 시간 차이가 23시간 59분을 초과하는 경우 조정한다.
            if (durationBetween.compareTo(maxDuration) > 0) {
                endTime = startTime.plusHours(23).plusMinutes(59);
            }
            startTime = applyUtcOffsetToKoreanTime(datetimeOffset, startTime);
            endTime = applyUtcOffsetToKoreanTime(datetimeOffset, endTime);
        }

        // Create activity.
        Activity activity = Activity.builder()
                .babyId(request.getBabyId())
                .type(request.getType())
                .type2(request.getType2())
                .memo(request.getMemo())
                .startTime(startTime)
                .endTime(endTime)
                .rightBreast(request.getRightBreast())
                .leftBreast(request.getLeftBreast())
                .breast(request.getBreast())
                .rightAmount(request.getRightAmount())
                .leftAmount(request.getLeftAmount())
                .amount(request.getAmount())
                .unit(request.getUnit())
                .unit2(request.getUnit2())
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build();
        activityRepository.save(activity);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new ActivityResponse(convertActivityToActivityDto(activity)));
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<ActivityResponse> updateActivity(int activityId, ActivityRequest activityRequest) {
        // Get Datetime-Offset header value, Timezone-Identifier header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activityRequest.getType() != null && !Objects.equals(activityRequest.getType(), activity.getType())) {
            activity.updateType(activityRequest.getType());
        }
        if (activityRequest.getType2() != null && !Objects.equals(activityRequest.getType2(), activity.getType2())) {
            activity.updateType2(activityRequest.getType2());
        }
        if (activityRequest.getMemo() != null && !Objects.equals(activityRequest.getMemo(), activity.getMemo())) {
            activity.updateMemo(activityRequest.getMemo());
        }
        if (activityRequest.getEndTime() != null) {
            activity.updateEndTime(applyUtcOffsetToKoreanTime(datetimeOffset, activityRequest.getEndTime()));
        } else {
            activity.updateEndTime(null);
        }
        if (activityRequest.getStartTime() != null) {
            // 시작 시각과 종료 시각의 차이가 최대 23시간 59분까지 가능하다.
            LocalDateTime startDatetime = activityRequest.getStartTime();
            LocalDateTime endDatetime = activityRequest.getEndTime();
            if (endDatetime == null) {
                startDatetime = applyUtcOffsetToKoreanTime(datetimeOffset, startDatetime);
                activity.updateStartTime(startDatetime);
            } else {
                // 최대 23시간 59분 차이까지 계산한다.
                Duration maxDuration = Duration.ofHours(23).plusMinutes(59);
                Duration durationBetween = Duration.between(startDatetime, endDatetime);

                // 시간 차이가 23시간 59분을 초과하는 경우 조정한다.
                if (durationBetween.compareTo(maxDuration) > 0) {
                    endDatetime = startDatetime.plusHours(23).plusMinutes(59);
                }
                startDatetime = applyUtcOffsetToKoreanTime(datetimeOffset, startDatetime);
                endDatetime = applyUtcOffsetToKoreanTime(datetimeOffset, endDatetime);
                activity.updateStartTime(startDatetime);
                activity.updateEndTime(endDatetime);
            }
        }
        if (activityRequest.getRightBreast() != null) {
            activity.updateRightBreast(activityRequest.getRightBreast());
        } else {
            activity.updateRightBreast(null);
        }
        if (activityRequest.getLeftBreast() != null) {
            activity.updateLeftBreast(activityRequest.getLeftBreast());
        } else {
            activity.updateLeftBreast(null);
        }
        if (activityRequest.getBreast() != null) {
            activity.updateBreast(activityRequest.getBreast());
        }
        if (activityRequest.getRightAmount() != null) {
            activity.updateRightAmount(activityRequest.getRightAmount());
        } else {
            activity.updateRightAmount(null);
        }
        if (activityRequest.getLeftAmount() != null) {
            activity.updateLeftAmount(activityRequest.getLeftAmount());
        } else {
            activity.updateLeftAmount(null);
        }
        if (activityRequest.getAmount() != null && !Objects.equals(activityRequest.getAmount(), activity.getAmount())) {
            activity.updateAmount(activityRequest.getAmount());
        }
        if (activityRequest.getUnit() != null && !Objects.equals(activityRequest.getUnit(), activity.getUnit())) {
            activity.updateUnit(activityRequest.getUnit());
        }
        if (activityRequest.getUnit2() != null && !Objects.equals(activityRequest.getUnit2(), activity.getUnit2())) {
            activity.updateUnit2(activityRequest.getUnit2());
        }
        activity.updateUtcOffset(String.valueOf(datetimeOffset.getOffset()));
        activity.updateTzId(timezoneIdentifier);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new ActivityResponse(convertActivityToActivityDto(activity)));
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> deleteActivity(int activityId) {
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        optionalActivity.ifPresent(activity -> activity.updateIsActive(false));

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), null);
    }

    /**
     * 마지막 현지 날짜를 한국 날짜로 변경 후 해당 날짜 미만으로 100개의 데이터를 가져와서 다시 현지 날짜로 변경 후 반환한다.
     *
     * @param lastDate 마지막 현지 날짜
     * @return 활동
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<ActivityListResponse> getActivities(LocalDate lastDate, int babyId) {
        List<ActivityWrapper> activityWrappersResponse = new ArrayList<>();
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        Baby baby = babyRepository.findByBabyId(babyId);
        LocalDate lastDateForReturn = lastDate;

        // 1. lastLocalDate 기준으로 현지 해당일의 시작 시각을 구하고 한국 시간으로 변경한다.
        LocalDateTime startKoreanDatetime = applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(lastDate, LocalTime.of(0, 0, 0)));

        // 2. 위에서 구한 startKoreanDatetime 미만으로 100개의 값을 가져온다.
        List<Activity> activities = activityRepository.findTop100ByBabyIdAndStartTimeLessThanAndIsActiveOrderByStartTimeDesc(babyId, startKoreanDatetime, true);

        // 3. 순서가 보장되는 LinkedHashSet에 값을 담는다.
        Set<Activity> activitySet = new LinkedHashSet<>(activities);

        // 4. 값 중에 마지막 값을 가져온다.
        Activity lastActivity = null;
        if (!activities.isEmpty()) {
            lastActivity = activities.get(activities.size() - 1);
        }
        if (lastActivity != null) { // 마지막 값이 있다면
            // 반환용 lastLocalDate에 값을 삽입한다.
            lastDateForReturn = applyUtcOffsetToLocalDate(datetimeOffset, lastActivity.getStartTime());

            // 해당 startDatetime 값을 현지 시간으로 변경 후 해당일의 시작 시각과 종료 시각을 구하고 한국 시간으로 변경한다.
            LocalDateTime localDatetime = applyUtcOffsetToLocalTime(datetimeOffset, lastActivity.getStartTime());
            LocalDateTime startKoreanDatetime2 = applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(localDatetime.toLocalDate(), LocalTime.of(0, 0, 0)));
            LocalDateTime endLocalDatetime2 = applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(localDatetime.toLocalDate(), LocalTime.of(23, 59, 59)));
            List<Activity> dailyRecords2 = activityRepository.findByBabyIdAndStartTimeBetweenAndIsActiveOrderByStartTimeDesc(babyId, startKoreanDatetime2, endLocalDatetime2, true);
            activitySet.addAll(dailyRecords2);
        }

        @Getter
        @EqualsAndHashCode
        class LocalDateKey {
            private final LocalDate localDate;

            public LocalDateKey(Activity activity) {
                this.localDate = applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime()).toLocalDate();
            }
        }

        // 5. LocalDateKey 기준으로 그룹화한다. 순서가 보장되는 LinkedHashMap을 이용한다.
        Map<LocalDateKey, List<Activity>> collect = activitySet.stream().collect(Collectors.groupingBy(LocalDateKey::new, LinkedHashMap::new, Collectors.toList()));
        for (LocalDateKey localDateKey : collect.keySet()) {
            List<ActivityDto> dailyRecordsResponse = new ArrayList<>();
//            System.out.println("key:value = " + localDateKey + ":" + collect.get(localDateKey));

            // d-day
            long dDay = ChronoUnit.DAYS.between(applyUtcOffsetToLocalTime(datetimeOffset, baby.getBirthday()).toLocalDate(), localDateKey.localDate);
            if (dDay >= 0) {
                dDay = dDay + 1;
            }

            // amount of things
            long powderedMilkAmount = (long) collect.get(localDateKey).stream().filter(v -> Objects.equals(v.getType(), "POWDERED_MILK")).mapToDouble(Activity::getAmount).sum();
            long babyFoodAmount = (long) collect.get(localDateKey).stream().filter(v -> Objects.equals(v.getType(), "BABY_FOOD")).mapToDouble(Activity::getAmount).sum();
            long milkAmount = (long) collect.get(localDateKey).stream().filter(v -> Objects.equals(v.getType(), "MILK")).mapToDouble(Activity::getAmount).sum();
            long snackAmount = (long) collect.get(localDateKey).stream().filter(v -> Objects.equals(v.getType(), "SNACK")).mapToDouble(Activity::getAmount).sum();

            // list
            for (Activity activity : collect.get(localDateKey)) {
                dailyRecordsResponse.add(convertActivityToActivityDto(activity));
            }
            ActivityWrapper activityWrapper = ActivityWrapper.builder()
                    .localDate(localDateKey.localDate)
                    .dDay(dDay)
                    .powderedMilkAmount(powderedMilkAmount)
                    .babyFoodAmount(babyFoodAmount)
                    .milkAmount(milkAmount)
                    .snackAmount(snackAmount)
                    .activities(dailyRecordsResponse)
                    .build();
            activityWrappersResponse.add(activityWrapper);
        }

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new ActivityListResponse(activityWrappersResponse, lastDateForReturn));
    }
}

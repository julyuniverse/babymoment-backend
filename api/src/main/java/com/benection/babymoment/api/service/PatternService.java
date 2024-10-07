package com.benection.babymoment.api.service;

import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.Status;
import com.benection.babymoment.api.dto.pattern.DailyPatternDto;
import com.benection.babymoment.api.dto.pattern.DailyPatternListResponse;
import com.benection.babymoment.api.dto.pattern.DailyPatternResponse;
import com.benection.babymoment.api.dto.pattern.PatternDto;
import com.benection.babymoment.api.entity.Activity;
import com.benection.babymoment.api.entity.Baby;
import com.benection.babymoment.api.enums.StatusCode;
import com.benection.babymoment.api.repository.ActivityRepository;
import com.benection.babymoment.api.repository.BabyRepository;
import com.benection.babymoment.api.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.benection.babymoment.api.util.ConvertUtils.convertActivityToPatternDtoList;
import static com.benection.babymoment.api.util.DateUtils.applyUtcOffsetToLocalTime;
import static com.benection.babymoment.api.util.HttpHeaderUtils.getDatetimeOffset;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PatternService {
    private final ActivityRepository activityRepository;
    private final BabyRepository babyRepository;

    /**
     * 일간 패턴을 반환한다.
     *
     * @param date   현지 날짜
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<DailyPatternResponse> getDailyPatterns(LocalDate date, int babyId) {
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        LocalDateTime startTime = DateUtils.applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(date, LocalTime.of(0, 0, 0)));
        LocalDateTime endTime = DateUtils.applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(date, LocalTime.of(23, 59, 59)));
        List<Activity> activities = activityRepository.listByBabyIdAndStartTimeBetweenOrEndTimeBetweenAndIsActiveOrderByStarTime(babyId, startTime, endTime, true);
        List<PatternDto> patternDtos = new ArrayList<>();
        for (Activity activity : activities) {
            patternDtos.addAll(convertActivityToPatternDtoList(activity, startTime, endTime));
        }
        Baby baby = babyRepository.findByBabyId(babyId);
        long dDay = ChronoUnit.DAYS.between(applyUtcOffsetToLocalTime(datetimeOffset, baby.getBirthday()).toLocalDate(), date);
        if (dDay >= 0) {
            dDay = dDay + 1;
        }

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new DailyPatternResponse(new DailyPatternDto(date, dDay, patternDtos)));
    }

    /**
     * 주간 패턴을 반환한다.
     *
     * @param startDate 현지 시작 날짜
     * @param endDate   현지 종료 날짜
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<DailyPatternListResponse> getWeeklyPatterns(LocalDate startDate, LocalDate endDate, int babyId) {
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        LocalDateTime startTime = DateUtils.applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)));
        LocalDateTime endTime = DateUtils.applyUtcOffsetToKoreanTime(datetimeOffset, LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));
        List<Activity> activities = activityRepository.listByBabyIdAndStartTimeBetweenOrEndTimeBetweenAndIsActiveOrderByStarTime(babyId, startTime, endTime, true);
        List<PatternDto> patternDtos = new ArrayList<>();
        for (Activity activity : activities) {
            patternDtos.addAll(convertActivityToPatternDtoList(activity, startTime, endTime));
        }

        // 일간 패턴에 날짜를 먼저 삽입한다.
        List<DailyPatternDto> dailyPatternDtos = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dailyPatternDtos.add(DailyPatternDto.builder()
                    .date(currentDate)
                    .patterns(new ArrayList<>())
                    .build());
            currentDate = currentDate.plusDays(1); // 하루 증가
        }

        // 패턴들을 각 날짜에 삽입한다.
        for (PatternDto patternDto : patternDtos) {
            for (DailyPatternDto dailyPatternDto : dailyPatternDtos) {
                if (dailyPatternDto.getDate().equals(patternDto.getDate())) {
                    dailyPatternDto.getPatterns().add(patternDto);
                    break;
                }
            }
        }

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new DailyPatternListResponse(dailyPatternDtos));
    }
}

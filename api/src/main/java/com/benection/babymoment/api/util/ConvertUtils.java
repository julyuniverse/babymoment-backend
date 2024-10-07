package com.benection.babymoment.api.util;

import com.benection.babymoment.api.dto.account.AccountDto;
import com.benection.babymoment.api.dto.activity.ActivityDto;
import com.benection.babymoment.api.dto.baby.BabyDto;
import com.benection.babymoment.api.dto.inquiry.InquiryDto;
import com.benection.babymoment.api.dto.pattern.PatternDto;
import com.benection.babymoment.api.entity.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.benection.babymoment.api.util.DateUtils.applyUtcOffsetToLocalTime;
import static com.benection.babymoment.api.util.HttpHeaderUtils.getDatetimeOffset;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class ConvertUtils {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static AccountDto convertAccountToAccountDto(Account account) {
        return new AccountDto(account.getAccountId(),
                account.getEmail(),
                account.getUsername());
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static BabyDto convertBabyToBabyDto(Baby baby, Relationship relationship) {
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        return BabyDto.builder()
                .babyId(baby.getBabyId())
                .name(baby.getName())
                .birthday(applyUtcOffsetToLocalTime(datetimeOffset, baby.getBirthday()))
                .gender(baby.getGender())
                .bloodType(baby.getBloodType())
                .relationshipType(relationship.getType())
                .imageFileName(baby.getImageFileName())
                .authority(relationship.getAuthority())
                .build();
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static ActivityDto convertActivityToActivityDto(Activity activity) {
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        LocalDateTime startTime = applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime());
        LocalDateTime endTime = activity.getEndTime() != null ? applyUtcOffsetToLocalTime(datetimeOffset, activity.getEndTime()) : null;

        return ActivityDto.builder()
                .activityId(activity.getActivityId())
                .babyId(activity.getBabyId())
                .localDate(startTime.toLocalDate())
                .type(activity.getType())
                .type2(activity.getType2())
                .memo(activity.getMemo())
                .startTime(startTime)
                .endTime(endTime)
                .rightBreast(activity.getRightBreast())
                .leftBreast(activity.getLeftBreast())
                .breast(activity.getBreast())
                .rightAmount(activity.getRightAmount())
                .leftAmount(activity.getLeftAmount())
                .amount(activity.getAmount())
                .unit(activity.getUnit())
                .unit2(activity.getUnit2())
                .build();
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static List<PatternDto> convertActivityToPatternDtoList(Activity activity, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        OffsetDateTime datetimeOffset = getDatetimeOffset(); // Get Datetime-Offset header value.
        List<PatternDto> patternDtos = new ArrayList<>();
        LocalTime startTime;
        LocalTime endTime;
        boolean isDateBefore = false; // Activity 내의 startTime이 파라미터 startTime보다 이전인지 확인
        boolean isDateAfter = false; // Activity 내의 endTime이 파라미터 endTime보다 이후인지 확인

        // 시작 시각
        if (activity.getStartTime().isBefore(startDatetime)) { // 시작 시각이 설정된 시작 시각 보다 작다면 설정된 시작 시각으로 변경
            startTime = applyUtcOffsetToLocalTime(datetimeOffset, startDatetime).toLocalTime();
            isDateBefore = true;
        } else {
            startTime = applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime()).toLocalTime();
        }

        // 종료 시각
        if (activity.getEndTime() == null) {
            LocalDateTime endDatetime2 = activity.getStartTime().plusMinutes(5); // 5분 더하기
            if (endDatetime2.isAfter(endDatetime)) { // 종료 시각이 설정된 종료 시각 보다 작다면 설정된 종료 시각으로 변경
                endTime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime).toLocalTime();
                isDateAfter = true;
            } else {
                endTime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime2).toLocalTime();
            }
        } else {
            if (activity.getStartTime().equals(activity.getEndTime())) { // 시작 시각과 종료 시각이 같다면
                LocalDateTime endDatetime2 = activity.getStartTime().plusMinutes(5); // 5분 더하기
                if (endDatetime2.isAfter(endDatetime)) { // 종료 시각이 설정된 종료 시각 보다 작다면 설정된 종료 시각으로 변경
                    endTime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime).toLocalTime();
                    isDateAfter = true;
                } else {
                    endTime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime2).toLocalTime();
                }
            } else {
                if (activity.getEndTime().isAfter(endDatetime)) { // 종료 시각이 설정된 종료 시각 보다 작다면 설정된 종료 시각으로 변경
                    endTime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime).toLocalTime();
                    isDateAfter = true;
                } else {
                    endTime = applyUtcOffsetToLocalTime(datetimeOffset, activity.getEndTime()).toLocalTime();
                }
            }
        }

        // 설정된 날짜보다 이전 또는 이후라면 하나의 Activity 객체만 반환한다.
        if (isDateBefore || isDateAfter) {
            patternDtos.add(PatternDto.builder()
                    .date(isDateBefore ? applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime()).toLocalDate().plusDays(1) : applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime()).toLocalDate())
                    .startTime(startTime)
                    .endTime(endTime)
                    .type(activity.getType())
                    .build());

            return patternDtos;
        }
        // 아니라면 한 개 또는 두 개의 Activity 객체를 반환한다.
        else {
            boolean isDifferent = false; // 시작 날짜와 종료 날짜가 다른지 확인

            // 1. 시작 시각, 종료 시각 세팅
            LocalDateTime startDatetime2 = activity.getStartTime();
            LocalDateTime endDatetime2 = activity.getEndTime();
            if (endDatetime2 == null) {
                endDatetime2 = startDatetime2.plusMinutes(5); // 5분 더하기
            }

            // 2. Activity의 startDatetime과 endDatetime을 현지 시각으로 변경
            LocalDateTime localStartDatetime = applyUtcOffsetToLocalTime(datetimeOffset, startDatetime2);
            LocalDateTime localEndDatetime = applyUtcOffsetToLocalTime(datetimeOffset, endDatetime2);

            // 3. 시작 날짜와 종료 날짜가 다른지 확인
            if (!localStartDatetime.toLocalDate().equals(localEndDatetime.toLocalDate())) {
                isDifferent = true;
            }

            // 시작 날짜와 종료 날짜가 다르다면 두 개의 Activity 객체를 반환한다.
            if (isDifferent) {
                LocalDateTime localStartDatetime1 = localStartDatetime;
                LocalDateTime localEndDatetime1 = LocalDateTime.of(localStartDatetime.toLocalDate(), LocalTime.of(23, 59, 59));
                LocalDateTime localStartDatetime2 = LocalDateTime.of(localEndDatetime.toLocalDate(), LocalTime.of(0, 0, 0));
                LocalDateTime localEndDatetime2 = localEndDatetime;
                patternDtos.add(PatternDto.builder()
                        .date(localStartDatetime1.toLocalDate())
                        .startTime(localStartDatetime1.toLocalTime())
                        .endTime(localEndDatetime1.toLocalTime())
                        .type(activity.getType())
                        .build());
                patternDtos.add(PatternDto.builder()
                        .date(localStartDatetime2.toLocalDate())
                        .startTime(localStartDatetime2.toLocalTime())
                        .endTime(localEndDatetime2.toLocalTime())
                        .type(activity.getType())
                        .build());

                return patternDtos;
            }
            // 시작 날짜와 종료 날짜가 같다면 하나의 Activity 객체를 반환한다.
            else {
                patternDtos.add(PatternDto.builder()
                        .date(applyUtcOffsetToLocalTime(datetimeOffset, activity.getStartTime()).toLocalDate())
                        .startTime(startTime)
                        .endTime(endTime)
                        .type(activity.getType())
                        .build());

                return patternDtos;
            }

//            // Activity의 endDatetime가 null이거거나 startDatetime과 endDatetime이 같다면 한 개의 Activity 객체를 반환
//            if (activity.getEndDatetime() == null || activity.getStartDatetime().equals(activity.getEndDatetime())) {
//                patternsResponse.add(Pattern.builder()
//                        .date(setLocalDatetime(offsetDatetime, activity.getStartDatetime()).toLocalDate())
//                        .startTime(startTime)
//                        .endTime(endTime)
//                        .type1(activity.getType1())
//                        .build());
//
//                return patternsResponse;
//            }
//            boolean isDifferent = false; // 시작 날짜와 종료 날짜가 다른지 확인
//
//            // 1. Activity의 startDatetime과 endDatetime을 현지 시각으로 변경
//            LocalDateTime localStartDatetime = setLocalDatetime(offsetDatetime, activity.getStartDatetime());
//            LocalDateTime localEndDatetime = setLocalDatetime(offsetDatetime, activity.getEndDatetime());
//
//            // 2. 시작 날짜와 종료 날짜가 다른지 확인
//            if (!localStartDatetime.toLocalDate().equals(localEndDatetime.toLocalDate())) {
//                isDifferent = true;
//            }
//
//            // 시작 날짜와 종료 날짜가 다르다면 두 개의 Activity 객체를 반환
//            if (isDifferent) {
//                LocalDateTime localStartDatetime1 = LocalDateTime.of(localStartDatetime.toLocalDate(), LocalTime.of(0, 0, 0));
//                LocalDateTime localStartDatetime2 = LocalDateTime.of(localStartDatetime.toLocalDate(), LocalTime.of(0, 0, 0));
//
//                return patternsResponse;
//            }
//            // 시작 날짜와 종료 날짜가 같다면 하나의 Activity 객체를 반환
//            else {
//                patternsResponse.add(Pattern.builder()
//                        .date(setLocalDatetime(offsetDatetime, activity.getStartDatetime()).toLocalDate())
//                        .startTime(startTime)
//                        .endTime(endTime)
//                        .type1(activity.getType1())
//                        .build());
//
//                return patternsResponse;
//            }
        }
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public static InquiryDto convertInquiryToInquiryDto(Inquiry inquiry) {
        return InquiryDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .inquiryDate(inquiry.getInquiryDate())
                .answer(inquiry.getAnswer())
                .answerDate(inquiry.getAnswerDate())
                .build();
    }
}

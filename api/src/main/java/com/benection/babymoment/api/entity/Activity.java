package com.benection.babymoment.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
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
    private Boolean isActive;
    private String utcOffset;
    private String tzId;

    @Builder
    public Activity(Integer babyId, String type, String type2, String memo, LocalDateTime startTime, LocalDateTime endTime, Float rightBreast, Float leftBreast, Float breast, Float rightAmount, Float leftAmount, Float amount, String unit, String unit2, Boolean isActive, String utcOffset, String tzId) {
        this.babyId = babyId;
        this.type = type;
        this.type2 = type2;
        this.memo = memo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rightBreast = rightBreast;
        this.leftBreast = leftBreast;
        this.breast = breast;
        this.rightAmount = rightAmount;
        this.leftAmount = leftAmount;
        this.amount = amount;
        this.unit = unit;
        this.unit2 = unit2;
        this.isActive = isActive;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
    }

    public void updateType(String type) {
        this.type = type;
    }

    public void updateType2(String type2) {
        this.type2 = type2;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateRightBreast(Float rightBreast) {
        this.rightBreast = rightBreast;
    }

    public void updateLeftBreast(Float leftBreast) {
        this.leftBreast = leftBreast;
    }

    public void updateBreast(Float breast) {
        this.breast = breast;
    }

    public void updateRightAmount(Float rightAmount) {
        this.rightAmount = rightAmount;
    }

    public void updateLeftAmount(Float leftAmount) {
        this.leftAmount = leftAmount;
    }

    public void updateAmount(Float amount) {
        this.amount = amount;
    }

    public void updateUnit(String unit) {
        this.unit = unit;
    }

    public void updateUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void updateUtcOffset(String utcOffset) {
        this.utcOffset = utcOffset;
    }

    public void updateTzId(String tzId) {
        this.tzId = tzId;
    }
}
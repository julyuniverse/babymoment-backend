package com.benection.babymoment.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@DynamicInsert // insert 시 null인 필드 제외
@DynamicUpdate // update 시 null인 필드 제외
public class Baby extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer babyId;
    private String name;
    private LocalDateTime birthday;
    private String gender;
    private String bloodType;
    private String imageFileName;
    private Boolean isDeleted;
    private String utcOffset;
    private String tzId;
    private LocalDateTime deletedAt;

    @Builder
    public Baby(String name, LocalDateTime birthday, String gender, String bloodType, String imageFileName, Boolean isDeleted, String utcOffset, String tzId, LocalDateTime deletedAt) {
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.bloodType = bloodType;
        this.imageFileName = imageFileName;
        this.isDeleted = isDeleted;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
        this.deletedAt = deletedAt;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public void updateGender(String gender) {
        this.gender = gender;
    }

    public void updateBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void updateImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}

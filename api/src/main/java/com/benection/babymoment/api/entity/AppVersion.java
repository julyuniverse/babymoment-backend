package com.benection.babymoment.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicInsert // insert 시 null인 필드 제외
@DynamicUpdate // update 시 null인 필드 제외
public class AppVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appVersionId;
    private String version;
    private String platform;
    private LocalDate releaseDate;
    private Boolean isMandatory;
    private Boolean isActive;
}

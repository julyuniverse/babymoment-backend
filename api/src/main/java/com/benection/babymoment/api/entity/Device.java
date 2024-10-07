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

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicInsert // insert 시 null인 필드 제외
@DynamicUpdate // update 시 null인 필드 제외
public class Device extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceId;
    private String uuid;
    private String model;
    private String systemName;
    private String systemVersion;
    private Integer accountId;
    private Integer babyId;

    @Builder
    public Device(String uuid, String model, String systemName, String systemVersion, Integer accountId, Integer babyId) {
        this.uuid = uuid;
        this.model = model;
        this.systemName = systemName;
        this.systemVersion = systemVersion;
        this.accountId = accountId;
        this.babyId = babyId;
    }

    public void updateModel(String model) {
        this.model = model;
    }

    public void updateSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void updateSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public void updateAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void updateBabyId(Integer babyId) {
        this.babyId = babyId;
    }
}

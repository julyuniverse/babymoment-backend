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
public class AuthenticationLog extends BaseEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authenticationLogId;
    private String type;
    private Integer deviceId;
    private Integer accountId;
    private Integer babyId;
    private String ip;
    private String userAgent;
    private String appVersion;
    private String utcOffset;
    private String tzId;

    @Builder
    public AuthenticationLog(String type, Integer deviceId, Integer accountId, Integer babyId, String ip, String userAgent, String appVersion, String utcOffset, String tzId) {
        this.type = type;
        this.deviceId = deviceId;
        this.accountId = accountId;
        this.babyId = babyId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.appVersion = appVersion;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
    }
}

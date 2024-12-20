package com.benection.babymoment.api.entity;

import jakarta.persistence.*;
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
@DynamicInsert
@DynamicUpdate
@Table(name = "authentication_logs")
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
    private String version;
    private String utcOffset;
    private String tzId;
    private LocalDateTime authenticatedAt;

    @Builder
    public AuthenticationLog(String type, Integer deviceId, Integer accountId, Integer babyId, String ip, String userAgent, String version, String utcOffset, String tzId, LocalDateTime authenticatedAt) {
        this.type = type;
        this.deviceId = deviceId;
        this.accountId = accountId;
        this.babyId = babyId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.version = version;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
        this.authenticatedAt = authenticatedAt;
    }
}

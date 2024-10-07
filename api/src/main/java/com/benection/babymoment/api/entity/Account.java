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
@DynamicInsert
@DynamicUpdate
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    private String email;
    private String password;
    private String username;
    private String authority;
    private Boolean isDeleted;
    private String utcOffset;
    private String tzId;
    private LocalDateTime deletedAt;

    @Builder
    public Account(String email, String password, String username, String authority, Boolean isDeleted, String utcOffset, String tzId, LocalDateTime deletedAt) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.authority = authority;
        this.isDeleted = isDeleted;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
        this.deletedAt = deletedAt;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}

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
@Table(name = "accounts")
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    private String userIdentifier;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String authority;
    private String utcOffset;
    private String tzId;
    private LocalDateTime deletedAt;
    private Boolean isDeleted;

    @Builder
    public Account(String userIdentifier, String email, String password, String firstName, String lastName, String authority, String utcOffset, String tzId, LocalDateTime deletedAt, Boolean isDeleted) {
        this.userIdentifier = userIdentifier;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authority = authority;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void updateLastName(String lastName) {
        this.lastName = lastName;
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}

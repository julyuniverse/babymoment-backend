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
public class Relationship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationshipId;
    private Integer accountId;
    private Integer babyId;
    private String type;
    private String authority;

    @Builder
    public Relationship(Integer accountId, Integer babyId, String type, String authority) {
        this.accountId = accountId;
        this.babyId = babyId;
        this.type = type;
        this.authority = authority;
    }

    public void updateType(String type) {
        this.type = type;
    }
}

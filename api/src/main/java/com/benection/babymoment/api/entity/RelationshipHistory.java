package com.benection.babymoment.api.entity;

import jakarta.persistence.*;
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
@DynamicInsert
@DynamicUpdate
@Table(name = "relationship_histories")
public class RelationshipHistory extends BaseEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationshipHistoryId;
    private String type;
    private Integer accountId;
    private Integer babyId;
    private String relationshipType;
    private String authority;
    private String utcOffset;
    private String tzId;

    @Builder
    public RelationshipHistory(String type, Integer accountId, Integer babyId, String relationshipType, String authority, String utcOffset, String tzId) {
        this.type = type;
        this.accountId = accountId;
        this.babyId = babyId;
        this.relationshipType = relationshipType;
        this.authority = authority;
        this.utcOffset = utcOffset;
        this.tzId = tzId;
    }
}

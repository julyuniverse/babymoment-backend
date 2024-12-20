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
@Table(name = "inquiries")
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inquiryId;
    private Integer accountId;
    private Integer babyId;
    private String title;
    private String content;
    private LocalDateTime inquiredAt;
    private String answer;
    private LocalDateTime answeredAt;

    @Builder
    public Inquiry(Integer accountId, Integer babyId, String title, String content, LocalDateTime inquiredAt, String answer, LocalDateTime answeredAt) {
        this.accountId = accountId;
        this.babyId = babyId;
        this.title = title;
        this.content = content;
        this.inquiredAt = inquiredAt;
        this.answer = answer;
        this.answeredAt = answeredAt;
    }
}

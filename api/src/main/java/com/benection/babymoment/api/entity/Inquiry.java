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
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inquiryId;
    private Integer accountId;
    private Integer babyId;
    private String title;
    private String content;
    private LocalDateTime inquiryDate;
    private String answer;
    private LocalDateTime answerDate;

    @Builder
    public Inquiry(Integer accountId, Integer babyId, String title, String content, LocalDateTime inquiryDate, String answer, LocalDateTime answerDate) {
        this.accountId = accountId;
        this.babyId = babyId;
        this.title = title;
        this.content = content;
        this.inquiryDate = inquiryDate;
        this.answer = answer;
        this.answerDate = answerDate;
    }
}

package com.benection.babymoment.api.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "babycode")
@ToString
public class BabyCode {
    @Id
    private String code;
    @Indexed
    private Integer accountId;
    @Indexed
    private Integer babyId;
    @TimeToLive
    private Long ttl;
}

package com.benection.babymoment.api.dto.baby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BabyCreationRequest {
    private Integer accountId;
    private String name;
    private LocalDateTime birthday;
    private String gender;
    private String bloodType;
    private String relationshipType;
    private MultipartFile image;
}

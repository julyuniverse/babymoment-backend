package com.benection.babymoment.api.config;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestController
@AllArgsConstructor
public class WebRestController {
    private Environment env;

    @GetMapping("/spring-profile")
    public String springProfile() {
        return Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse("");
    }
}

package com.benection.babymoment.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Collections;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@SpringBootApplication
@ServletComponentScan(basePackages = {"com.benection.babymoment.api.config"})
@ComponentScan(basePackages = {"com.benection.babymoment.common", "com.benection.babymoment.api"})
@EnableJpaAuditing
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApiApplication.class);

        // application 파일 내 프로필은 기본적으로 병합처리되고 중복 발생 시 마지막 파일의 설정 기준으로 덮어 쓰인다. 중요도가 높은 파일을 역순으로 정렬한다.
        application.setDefaultProperties(Collections.singletonMap("spring.config.name", "application-common, application-api"));
        application.run(args);
    }
}

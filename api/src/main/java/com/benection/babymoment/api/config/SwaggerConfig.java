package com.benection.babymoment.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    final private BuildProperties buildProperties;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version(buildProperties.getVersion())
                .title("BabyMoment")
                .description("개발 서버 Base URL: http://100.100.100.100:1004");

//        // 인가 방식 구성
//        SecurityScheme auth = new SecurityScheme()
//                .type(SecurityScheme.Type.APIKEY)
//                .in(SecurityScheme.In.HEADER) // 헤더 인증 방식 설정
//                .name("x-api-key");
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList("basicAuth");

        return new OpenAPI()
//                .components(new Components().addSecuritySchemes("basicAuth", auth))
//                .addSecurityItem(securityRequirement)
                .info(info);
    }

    @Bean
    public OpenApiCustomizer customOpenApiHeader() {
        Parameter authorization = new Parameter()
                .name("Authorization")
                .in("header")
                .example("Bearer {accessToken}")
                .description("인증 토큰")
                .required(true)
                .schema(new StringSchema());
        Parameter acceptLanguage = new Parameter()
                .name("Accept-Language")
                .in("header")
                .example("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .description("언어 코드(ISO 639-1) or 언어 코드(ISO 639-1)와 국가 코드(ISO 3166-1 alpha-2)를 하이픈(-)으로 조합")
                .required(true)
                .schema(new StringSchema());
        Parameter datetimeOffset = new Parameter()
                .name("Datetime-Offset")
                .in("header")
                .example("2024-08-13T16:44:44+09:00")
                .description("ISO-8601 형식(yyyy-MM-dd'T'HH:mm:ssXXX)")
                .required(true)
                .schema(new StringSchema());
        Parameter timezoneIdentifier = new Parameter()
                .name("Timezone-Identifier")
                .in("header")
                .example("Asia/Seoul")
                .description("IANA Time Zone Identifier or Windows Time Zone ID")
                .required(true)
                .schema(new StringSchema());
        Parameter appVersion = new Parameter()
                .name("App-Version")
                .in("header")
                .example("1.1.1")
                .description("클라이언트 앱 버전")
                .required(true)
                .schema(new StringSchema());
        Parameter platform = new Parameter()
                .name("Platform")
                .in("header")
                .example("iOS")
                .description("플랫폼(Android, iOS)")
                .required(true)
                .schema(new StringSchema());
        Parameter deviceId = new Parameter()
                .name("Device-Id")
                .in("header")
                .example("1")
                .description("디바이스 고유 식별 아이디")
                .required(true)
                .schema(new StringSchema());

        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/api/v1/auth/login/uuid")) {
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                );
            } else if (path.startsWith("/api/v1/auth/login/social")) {
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                        .addParametersItem(deviceId)
                );
            } else if (path.startsWith("/api/v1/auth/signup/email")) {
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                        .addParametersItem(deviceId)
                );
            } else if (path.startsWith("/api/v1/auth/login/email")) {
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                        .addParametersItem(deviceId)
                );
            } else if (path.startsWith("/api/v1/auth/logout")) {
                Parameter optionalAuthorization = new Parameter()
                        .name("Authorization")
                        .in("header")
                        .example("Bearer {accessToken}")
                        .description("인증 토큰")
                        .schema(new StringSchema());
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(optionalAuthorization)
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                        .addParametersItem(deviceId)
                );
            } else {
                pathItem.readOperations().forEach(operation -> operation
                        .addParametersItem(authorization)
                        .addParametersItem(acceptLanguage)
                        .addParametersItem(datetimeOffset)
                        .addParametersItem(timezoneIdentifier)
                        .addParametersItem(appVersion)
                        .addParametersItem(platform)
                        .addParametersItem(deviceId)
                );
            }
        });
    }

    @Bean
    GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(customOpenApiHeader())
                .build();
    }

    // 스웨거 ui에서 localTime이 hour, minute, second, nano로 표현된다. 클라이언트에서 서버 요청 시 string type으로 보낼 수 있도록 출력 표현을 스웨거 ui에서 string type으로 표현한다.
    static {
        // LocalTime
        // 1번 방법 - 이 방법을 사용하면 현재 시간으로 출력된다.
        Schema<LocalTime> schema = new Schema<>();
        schema.example(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, schema);

        // 2번 방법 - 이 방법을 사용하면 단순하게 "string"으로 표현한다.
//        SpringDocUtils.getConfig().replaceWithClass(LocalTime.class, String.class);

        // LocalDateTime
//        Schema<LocalDateTime> schema2 = new Schema<>();
//        schema2.example(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class, schema2);
    }
}

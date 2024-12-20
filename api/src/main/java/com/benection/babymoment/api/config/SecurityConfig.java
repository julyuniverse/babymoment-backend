package com.benection.babymoment.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 회원가입, 로그인, 로그아웃 등 인증 관련 api(auth)는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll로 설정한다.
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api-docs/**").permitAll() // swagger ui
                        .requestMatchers("/swagger-ui/**").permitAll() // swagger ui
                        .requestMatchers("/spring-profile/**").permitAll() // spring profile
                        .requestMatchers("/actuator/**").permitAll() // actuator
                        .requestMatchers("/*/auth/**").permitAll() // auth api
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                // 시큐리티는 기본적으로 세션을 사용한다.
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless로 설정한다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // TokenFilter 삽입한다.
                // TokenFilter가 UsernamePasswordAuthenticationFilter보다 먼저 실행되도록 설정한다.
                .addFilterBefore(new TokenFilter(tokenProvider, redisService), UsernamePasswordAuthenticationFilter.class)

                // exception handling 할 때 직접 만든 클래스를 적용한다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                        .accessDeniedHandler(tokenAccessDeniedHandler)
                );

        return http.build();
    }
}

//package com.benection.babymoment.api.config;
//
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @author Lee Taesung
// * @since 1.0
// */
//@Component
//public class SocialAuthenticationProvider implements AuthenticationProvider {
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String idToken = (String) authentication.getPrincipal();
//
//        // ID Token 검증 로직 추가 (예: Google/Apple)
//        if (!isValidIdToken(idToken)) {
//            throw new RuntimeException("Invalid ID Token");
//        }
//
//        // 인증 성공 시, 인증된 Authentication 객체 반환
//        return new SocialAuthenticationToken(idToken, List.of(new SimpleGrantedAuthority("ROLE_USER")));
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
//    }
//
//    private boolean isValidIdToken(String idToken) {
//        // ID Token 검증 로직 (구현 필요)
//        return idToken != null && !idToken.isEmpty();
//    }
//}

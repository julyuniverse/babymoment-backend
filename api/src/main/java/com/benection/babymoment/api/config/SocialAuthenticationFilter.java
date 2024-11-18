//package com.benection.babymoment.api.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//
//import java.io.IOException;
//
///**
// * @author Lee Taesung
// * @since 1.0
// */
//public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//    private final AuthenticationProvider authenticationProvider;
//
//    public SocialAuthenticationFilter(AuthenticationProvider authenticationProvider) {
//        super("/api/*/social-auth/**"); // 소셜 로그인 경로 설정
//        this.authenticationProvider = authenticationProvider;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws RuntimeException {
//        String idToken = request.getParameter("idToken");
//
//        if (idToken == null || idToken.isEmpty()) {
//            throw new RuntimeException("ID Token is missing");
//        }
//
//        SocialAuthenticationToken authRequest = new SocialAuthenticationToken(idToken);
//        return authenticationProvider.authenticate(authRequest);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().write("Authentication Failed: " + failed.getMessage());
//    }
//}

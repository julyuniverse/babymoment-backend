package com.benection.babymoment.api.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    // Constructor for unauthenticated token (only principal)
    public SocialAuthenticationToken(Object principal) {
        super(null); // No authorities yet
        this.principal = principal;
        setAuthenticated(false); // Initially unauthenticated
    }

    // Constructor for authenticated token (principal and authorities)
    public SocialAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true); // Authenticated state
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials required
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}

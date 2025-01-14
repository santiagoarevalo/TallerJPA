package com.example.jpa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import javax.security.auth.Subject;
import java.util.Collection;

public class CustomAuthentication implements Authentication {

    private final Authentication authentication;

    private final String icesiUserId;

    public CustomAuthentication(Authentication authentication, String icesiUserId) {
        this.authentication = authentication;
        this.icesiUserId = icesiUserId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authentication.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return authentication.getCredentials();
    }

    @Override
    public Object getDetails() {
        return authentication.getDetails();
    }

    @Override
    public Object getPrincipal() {
        return authentication.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        return authentication.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authentication.setAuthenticated(isAuthenticated);
    }

    @Override
    public String getName() {
        return authentication.getName();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }

    //Get userId
    public String getIcesiUserId() {
        return icesiUserId;
    }
}

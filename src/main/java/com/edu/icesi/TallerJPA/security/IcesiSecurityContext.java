package com.edu.icesi.TallerJPA.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class IcesiSecurityContext {

    public static String getCurrentUserId() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .getToken().getClaimAsString("userId");
    }

    public static String getCurrentRol(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }
}

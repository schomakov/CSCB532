package com.nbu.CSCB532.global;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Helper class for role checks in views.
 */
public class AccessControlConfig {
    public static boolean isAdmin() {
        return hasRole("ADMINISTRATOR");
    }

    public static boolean isEmployee() {
        return hasRole("EMPLOYEE");
    }

    public static boolean isClient() {
        return hasRole("CLIENT");
    }

    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    private static boolean hasRole(String role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
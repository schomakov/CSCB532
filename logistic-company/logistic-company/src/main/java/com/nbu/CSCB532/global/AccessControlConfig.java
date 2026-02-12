package com.nbu.CSCB532.global;

import org.springframework.security.core.Authentication;
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

    /** True ако потребителят е логнат (не е анонимен). */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && auth.getPrincipal() != null
                && !"anonymousUser".equals(auth.getPrincipal().toString());
    }

    public static String getUsername() {
        if (!isAuthenticated()) {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    private static boolean hasRole(String role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
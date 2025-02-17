package com.financetrackingbackend.util;


import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AuthenticationUtil {
    private static final String AUTH_ERROR_MSG = "Problem authenticating user";
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        throw new UsernameNotFoundException(AUTH_ERROR_MSG);
    }
}

package com.financetrackingbackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AuthenticationUtilTest {
    private final AuthenticationUtil authenticationUtil = new AuthenticationUtil();
    private static final String AUTH_ERROR_MSG = "Problem authenticating user";

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_returnsUsername_whenAuthenticated() {
        String expectedUsername = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(expectedUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String actualUsername = authenticationUtil.getCurrentUsername();
        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    void getCurrentUsername_throwsException_whenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                authenticationUtil::getCurrentUsername
        );
        assertEquals(AUTH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void getCurrentUsername_throwsException_whenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                authenticationUtil::getCurrentUsername
        );
        assertEquals(AUTH_ERROR_MSG, exception.getMessage());
    }
}

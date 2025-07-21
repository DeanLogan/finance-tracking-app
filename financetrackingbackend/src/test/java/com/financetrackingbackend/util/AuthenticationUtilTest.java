package com.financetrackingbackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationUtilTest {
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthenticationUtil authenticationUtil;

    private static final String AUTH_ERROR_MSG = "Problem authenticating user";
    private static final String EXPECTED_USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_returnsUsernameWhenAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EXPECTED_USERNAME);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String actualUsername = authenticationUtil.getCurrentUsername();
        assertEquals(EXPECTED_USERNAME, actualUsername);
    }

    @Test
    void getCurrentUsername_throwsExceptionWhenAuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                authenticationUtil::getCurrentUsername
        );
        assertEquals(AUTH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void getCurrentUsername_throwsExceptionWhenNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                authenticationUtil::getCurrentUsername
        );
        assertEquals(AUTH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void getCurrentUsername_throwsExceptionWhenSecurityContextIsNull() {
        SecurityContextHolder.clearContext();

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                authenticationUtil::getCurrentUsername
        );
        assertEquals(AUTH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void getCurrentUsername_throwsExceptionWhenAuthenticationThrowsException() {
        when(securityContext.getAuthentication()).thenThrow(new RuntimeException("Unexpected error"));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(RuntimeException.class, authenticationUtil::getCurrentUsername);
    }
    
}

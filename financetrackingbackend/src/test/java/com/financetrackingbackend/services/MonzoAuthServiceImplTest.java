package com.financetrackingbackend.services;

import com.financetrackingbackend.configuration.MonzoConfig;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import com.financetrackingbackend.services.impl.MonzoAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonzoAuthServiceImplTest {
    @Mock
    private MonzoConfig config;
    @InjectMocks
    private MonzoAuthServiceImpl monzoAuthService;

    private static final String AUTH_URL = "https://auth.monzo.com";
    private static final String ID = "123";
    private static final String STATE_TOKEN = "123abc";
    private static final String REDIRECT_URL = "http://localhost:8080/monzo/oauth/callback.html";
    private static final String EXPECTED_URL = "https://auth.monzo.com?client_id=123&redirect_uri=http://localhost:8080/monzo/oauth/callback.html&response_type=code&state=123abc";
    private static final String EXPECTED_ERROR_MSG = "Failed to generate state token";

    @Test
    void buildMonzoAuthUrl_successfullyBuildsAuthUrl() {
        when(config.getClientId()).thenReturn(ID);
        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getAuthUrl()).thenReturn(AUTH_URL);
        String result = monzoAuthService.buildMonzoAuthUrl(STATE_TOKEN);
        assertEquals(EXPECTED_URL, result);
    }

    @Test
    void buildMonzoAuthUrl_throwsWhenStateIsNull() {
        when(config.getClientId()).thenReturn(ID);
        when(config.getRedirectUrl()).thenReturn(REDIRECT_URL);
        when(config.getAuthUrl()).thenReturn(AUTH_URL);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> monzoAuthService.buildMonzoAuthUrl(null));
        assertEquals(EXPECTED_ERROR_MSG, e.getMessage());
    }
}

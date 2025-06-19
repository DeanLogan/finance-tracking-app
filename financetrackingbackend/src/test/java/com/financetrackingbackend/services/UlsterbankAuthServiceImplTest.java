package com.financetrackingbackend.services;

import com.example.model.UlsterbankAccessToken;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.services.impl.UlsterbankAuthServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UlsterbankAuthServiceImplTest {
    @Mock
    private UlsterbankConfig ulsterbankConfig;
    @Mock
    private UlsterbankDao ulsterbankDao;
    @InjectMocks
    private UlsterbankAuthServiceImpl ulsterbankAuthService;

    private static final String TOKEN_STRING = "token";
    private static final String REFRESH_TOKEN = "refresh_token";

    @Test
    void refreshAccessToken_callsUlsterbankDaoRefreshToken() {
        UlsterbankAccessToken accessToken = Instancio.create(UlsterbankAccessToken.class);
        when(ulsterbankDao.tokenRequest(anyString(), anyString())).thenReturn(accessToken);
        UlsterbankAccessToken result = ulsterbankAuthService.refreshAccessToken(TOKEN_STRING);
        assertEquals(result, accessToken);
        verify(ulsterbankDao).tokenRequest(TOKEN_STRING, REFRESH_TOKEN);
    }
    
}

package com.financetrackingbackend.services;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankData;
import com.example.model.UlsterbankGeneralResponse;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import com.financetrackingbackend.services.impl.UlsterbankAuthServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static final String AUTH_CODE = "authorization_code";
    private static final String AUTH_URL = "https://api.sandbox.ulsterbank.co.uk/authorize";
    private static final String ID = "123";
    private static final String REDIRECT_URL = "http://localhost:8080/ulsterbank/oauth/callback.html";
    private static final String EXPECTED_REDIRECT_URL = "https://api.sandbox.ulsterbank.co.uk/authorize?client_id=123&response_type=code+id_token&scope=openid+accounts&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fulsterbank%2Foauth%2Fcallback.html&request=123";
    private static final String UB_NULL_RESPONSE_ERROR_MSG = "UB Response is null";

    @Test
    void refreshAccessToken_callsUlsterbankDaoRefreshToken() {
        UlsterbankAccessToken accessToken = Instancio.create(UlsterbankAccessToken.class);
        when(ulsterbankDao.tokenRequest(anyString(), anyString())).thenReturn(accessToken);
        UlsterbankAccessToken result = ulsterbankAuthService.refreshAccessToken(TOKEN_STRING);
        assertEquals(accessToken, result);
        verify(ulsterbankDao).tokenRequest(TOKEN_STRING, REFRESH_TOKEN);
    }

    @Test
    void getAccessToken_callsUlsterbankDaoGetToken() {
        UlsterbankAccessToken accessToken = Instancio.create(UlsterbankAccessToken.class);
        when(ulsterbankDao.tokenRequest(anyString(), anyString())).thenReturn(accessToken);
        UlsterbankAccessToken result = ulsterbankAuthService.getAccessToken(TOKEN_STRING);
        assertEquals(accessToken, result);
        verify(ulsterbankDao).tokenRequest(TOKEN_STRING, AUTH_CODE);
    }

    @Test
    void getRedirectUrl_correctlyFormsUrl() {
        when(ulsterbankConfig.getAuthUrl()).thenReturn(AUTH_URL);
        when(ulsterbankConfig.getClientId()).thenReturn(ID);
        when(ulsterbankConfig.getRedirectUrl()).thenReturn(REDIRECT_URL);
        String result = ulsterbankAuthService.getRedirectUrl(ID);
        assertEquals(EXPECTED_REDIRECT_URL, result);
    }

    @Test
    void extractConsentId_extractsTheConsentIdFromResponse() {
        UlsterbankData data = Instancio.of(UlsterbankData.class)
                .set(field(UlsterbankData::getConsentId), ID)
                .create();
        UlsterbankGeneralResponse response = Instancio.of(UlsterbankGeneralResponse.class)
                .set(field(UlsterbankGeneralResponse::getData), data)
                .create();

        String result = ulsterbankAuthService.extractConsentId(response);

        assertEquals(ID, result);
    }

    @Test
    void extractConsentId_handlesNullUlsterbankData() {
        UlsterbankGeneralResponse response = Instancio.of(UlsterbankGeneralResponse.class)
                .set(field(UlsterbankGeneralResponse::getData), null)
                .create();

        assertThrows(ServiceUnavailableException.class, () -> ulsterbankAuthService.extractConsentId(response));
    }

    @Test
    void extractConsentId_handlesNullUlsterbankresponse() {
        assertThrows(ServiceUnavailableException.class, () -> ulsterbankAuthService.extractConsentId(null));
    }
    
}

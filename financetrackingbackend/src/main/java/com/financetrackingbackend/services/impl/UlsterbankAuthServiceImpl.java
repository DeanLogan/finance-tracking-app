package com.financetrackingbackend.services.impl;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankGeneralResponse;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import com.financetrackingbackend.services.UlsterbankAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UlsterbankAuthServiceImpl implements UlsterbankAuthService {
    private final UlsterbankConfig config;
    private final UlsterbankDao ulsterbankDao;
    private static final String REDIRECT_URI = "http://localhost:8080/ulsterbank/oauth/callback.html";
    private static final String BASE_URL = "https://api.sandbox.ulsterbank.co.uk/authorize";
    private static final String RESPONSE_TYPE = "code id_token";
    private static final String SCOPE = "openid accounts";
    private static final String AUTH_URL_TEMPLATE = "%s?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s&request=%s";
    private static final String REFRESH_GRANT_TYPE = "refresh_token";
    private static final String AUTH_GRANT_TYPE = "authorization_code";

    @Override
    public UlsterbankAccessToken refreshAccessToken(String refreshToken) {
        return ulsterbankDao.tokenRequest(refreshToken, REFRESH_GRANT_TYPE);
    }

    @Override
    public UlsterbankAccessToken getAccessToken(String code) {
        return ulsterbankDao.tokenRequest(code, AUTH_GRANT_TYPE);
    }

    @Override
    public String getRedirectUrl(String consentId) {
        String clientId = config.getClientId();

        return String.format(AUTH_URL_TEMPLATE,
                BASE_URL,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(RESPONSE_TYPE, StandardCharsets.UTF_8),
                URLEncoder.encode(SCOPE, StandardCharsets.UTF_8),
                URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8),
                URLEncoder.encode(consentId, StandardCharsets.UTF_8)
        );
    }

    @Override
    public String extractConsentId(UlsterbankGeneralResponse response) {
        if(response == null || response.getData() == null) {
            throw new ServiceUnavailableException("UB Response is null");
        }
        return response.getData().getConsentId();
    }
}

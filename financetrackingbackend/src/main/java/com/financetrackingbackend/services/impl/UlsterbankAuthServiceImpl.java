package com.financetrackingbackend.services.impl;

import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankGeneralResponse;
import com.financetrackingbackend.services.UlsterbankAuthService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UlsterbankAuthServiceImpl implements UlsterbankAuthService {
    private final Dotenv dotenv;
    private final UlsterbankDao ulsterbankDao;
    @Override
    public UlsterbankAccessToken refreshAccessToken(String refreshToken) {
        return ulsterbankDao.tokenRequest(refreshToken, "refresh_token");
    }

    @Override
    public UlsterbankAccessToken getAccessToken(String code) {
        return ulsterbankDao.tokenRequest(code, "authorization_code");
    }

    @Override
    public String getRedirectUrl(String consentId) {
        String clientId = dotenv.get("ULSTER_BANK_CLIENT_ID");
        return formAuthorizationUrl(clientId, "http://localhost:8080/ulsterbank/oauth/callback.html", consentId);
    }

    @Override
    public String extractConsentId(UlsterbankGeneralResponse response) {
        return response.getData().getConsentId();
    }

    private String formAuthorizationUrl(String clientId, String redirectUri, String consentId) {
        String baseUrl = "https://api.sandbox.ulsterbank.co.uk/authorize";
        String responseType = "code id_token";
        String scope = "openid accounts";

        return String.format("%s?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s&request=%s",
                baseUrl,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(responseType, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(consentId, StandardCharsets.UTF_8)
        );
    }
}

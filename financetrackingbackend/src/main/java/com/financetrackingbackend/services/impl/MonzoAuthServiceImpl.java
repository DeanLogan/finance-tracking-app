package com.financetrackingbackend.services.impl;

import com.financetrackingbackend.schemas.monzo.MonzoAccessToken;
import com.financetrackingbackend.services.MonzoAuthService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MonzoAuthServiceImpl implements MonzoAuthService {
    private final Dotenv dotenv;

    @Override
    public String buildMonzoAuthUrl(String state) {
        String clientId = dotenv.get("MONZO_CLIENT_ID");
        String redirectUri = dotenv.get("MONZO_REDIRECT_URI");

        if (!StringUtils.isNoneBlank(clientId, redirectUri)) {
            throw new IllegalStateException("Environment variables are not configured");
        }

        if (state == null) {
            throw new IllegalStateException("Failed to generate state token");
        }

        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("auth.monzo.com")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    @Override
    public MonzoAccessToken getMonzoAccessToken(String grantType, String clientId, String clientSecret, String authCode, String redirectUri) {
        return null;
    }
}

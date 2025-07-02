package com.financetrackingbackend.services.impl;

import com.financetrackingbackend.configuration.MonzoConfig;
import com.financetrackingbackend.services.MonzoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.financetrackingbackend.util.AppConstants.CLIENT_ID;
import static com.financetrackingbackend.util.AppConstants.CODE;
import static com.financetrackingbackend.util.AppConstants.FAILED_TO_GENERATE_STATE;
import static com.financetrackingbackend.util.AppConstants.REDIRECT_URI;
import static com.financetrackingbackend.util.AppConstants.RESPONSE_TYPE;
import static com.financetrackingbackend.util.AppConstants.STATE;

@Service
@RequiredArgsConstructor
public class MonzoAuthServiceImpl implements MonzoAuthService {
    private final MonzoConfig config;

    @Override
    public String buildMonzoAuthUrl(String state) {
        String clientId = config.getClientId();
        String redirectUri = config.getRedirectUrl();
        String authUri = config.getAuthUrl();

        if (state == null) {
            throw new IllegalStateException(FAILED_TO_GENERATE_STATE);
        }

        return UriComponentsBuilder.newInstance()
                .uri(URI.create(authUri))
                .queryParam(CLIENT_ID, clientId)
                .queryParam(REDIRECT_URI, redirectUri)
                .queryParam(RESPONSE_TYPE, CODE)
                .queryParam(STATE, state)
                .build()
                .toUriString();
    }

}

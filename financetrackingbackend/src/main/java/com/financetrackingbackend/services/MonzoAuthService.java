package com.financetrackingbackend.services;

import com.financetrackingbackend.monzo.schema.MonzoAccessToken;

public interface MonzoAuthService {
    String buildMonzoAuthUrl(String state);
    MonzoAccessToken getMonzoAccessToken(String grantType, String clientId, String clientSecret, String authCode, String redirectUri);
}

package com.financetrackingbackend.services;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankGeneralResponse;

public interface UlsterbankAuthService {
    UlsterbankAccessToken refreshAccessToken(String refreshToken);
    UlsterbankAccessToken getAccessToken(String code);
    String getRedirectUrl(String consentId);
    String extractConsentId(UlsterbankGeneralResponse response);

}

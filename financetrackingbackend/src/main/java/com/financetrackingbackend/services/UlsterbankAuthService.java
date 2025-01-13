package com.financetrackingbackend.services;

import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;

public interface UlsterbankAuthService {
    UlsterbankAccessToken refreshAccessToken(String refreshToken);
    UlsterbankAccessToken getAccessToken(String code);
    String getRedirectUrl(String consentId);
    String extractConsentId(UlsterbankConsentResponse response);

}

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

import static com.financetrackingbackend.util.AppConstants.AUTH_CODE;
import static com.financetrackingbackend.util.AppConstants.AUTH_URL_TEMPLATE;
import static com.financetrackingbackend.util.AppConstants.CODE_ID_TOKEN;
import static com.financetrackingbackend.util.AppConstants.OPENID_ACCOUNTS;
import static com.financetrackingbackend.util.AppConstants.REFRESH_TOKEN;
import static com.financetrackingbackend.util.AppConstants.UB_BASE_URL;
import static com.financetrackingbackend.util.AppConstants.UB_NULL_RESPONSE_ERROR_MSG;
import static com.financetrackingbackend.util.AppConstants.UB_REDIRECT_URI;

@Service
@RequiredArgsConstructor
public class UlsterbankAuthServiceImpl implements UlsterbankAuthService {
    private final UlsterbankConfig config;
    private final UlsterbankDao ulsterbankDao;

    @Override
    public UlsterbankAccessToken refreshAccessToken(String refreshToken) {
        return ulsterbankDao.tokenRequest(refreshToken, REFRESH_TOKEN);
    }

    @Override
    public UlsterbankAccessToken getAccessToken(String code) {
        return ulsterbankDao.tokenRequest(code, AUTH_CODE);
    }

    @Override
    public String getRedirectUrl(String consentId) {
        String clientId = config.getClientId();

        return String.format(AUTH_URL_TEMPLATE,
                UB_BASE_URL,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(CODE_ID_TOKEN, StandardCharsets.UTF_8),
                URLEncoder.encode(OPENID_ACCOUNTS, StandardCharsets.UTF_8),
                URLEncoder.encode(UB_REDIRECT_URI, StandardCharsets.UTF_8),
                URLEncoder.encode(consentId, StandardCharsets.UTF_8)
        );
    }

    @Override
    public String extractConsentId(UlsterbankGeneralResponse response) {
        if(response == null || response.getData() == null) {
            throw new ServiceUnavailableException(UB_NULL_RESPONSE_ERROR_MSG);
        }
        return response.getData().getConsentId();
    }
}

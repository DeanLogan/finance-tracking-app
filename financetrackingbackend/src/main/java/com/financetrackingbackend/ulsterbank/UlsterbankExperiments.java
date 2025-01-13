package com.financetrackingbackend.ulsterbank;

import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankBalance;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UlsterbankExperiments {
    private final WebClient webClient;
    private final Dotenv dotenv;
    private final UlsterbankDao ulsterbankDao;

    public String extractConsentId(UlsterbankConsentResponse response) {
        return response.getData().getConsentId();
    }

    public String extractRedirectUrl(UlsterbankConsentResponse response) {
        return response.getLinks().getSelf();
    }

    public String getRedirectUrl(String consentId) {
        String clientId = dotenv.get("ULSTER_BANK_CLIENT_ID");
        return formAuthorizationUrl(clientId, "http://localhost:8080/ulsterbank/oauth/callback.html", consentId);
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

    public UlsterbankAccessToken getAccessToken(String code) {
        return ulsterbankDao.tokenRequest(code, "authorization_code");
    }

    public UlsterbankAccessToken refreshAccessToken(String refreshToken) {
        return ulsterbankDao.tokenRequest(refreshToken, "refresh_token");
    }

    private UlsterbankBalance getBalanceForAccount(String accessToken, String accountId) {
        return ulsterbankDao.getBalances(accessToken, accountId).get(0);
    }

    public float getBalanceForAllAccounts(String accessToken) {
        List<UlsterbankAccount> accountList = ulsterbankDao.getAccounts(accessToken);
        float amount = 0;
        for(UlsterbankAccount account : accountList) {
            UlsterbankBalance balance = getBalanceForAccount(accessToken, account.getAccountId());
            amount += balance.getAmount().getAmount();
        }
        return amount;
    }
}

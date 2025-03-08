package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.schemas.monzo.MonzoAccessToken;
import com.financetrackingbackend.schemas.monzo.MonzoAccount;
import com.financetrackingbackend.schemas.monzo.MonzoAccounts;
import com.financetrackingbackend.schemas.monzo.MonzoPots;
import com.financetrackingbackend.schemas.monzo.MonzoTransactionsResponse;
import com.financetrackingbackend.schemas.monzo.WhoAmI;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;

@Component
public class MonzoDaoImpl implements MonzoDao {
    private final String REFRESH_TOKEN = "refresh_token";
    private final String AUTHORISATION_CODE = "authorization_code";
    private final String HOST = "api.monzo.com";
    private final String REDIRECT_URI = "http://localhost:8080/monzo/oauth/callback";

    private final WebClient webClient;
    private final Dotenv dotenv;

    public MonzoDaoImpl(WebClient.Builder webClientBuilder, Dotenv dotenv) {
        this.webClient = webClientBuilder.baseUrl("https://api.monzo.com").build();
        this.dotenv = dotenv;
    }

    @Override
    public MonzoAccessToken exchangeAuthCode(String authCode) {
        if (StringUtils.isAnyBlank(authCode)) {
            throw new IllegalStateException("Required information for request is blank");
        }

        MultiValueMap<String, String> formData = buildFormData(AUTHORISATION_CODE, authCode, REDIRECT_URI);
        return fetchAccessToken(formData);
    }

    @Override
    public MonzoAccessToken refreshAccessToken(String refreshToken) {
        if (StringUtils.isAnyBlank(refreshToken)) {
            throw new IllegalStateException("Required information for request is blank");
        }

        MultiValueMap<String, String> formData = buildFormData(REFRESH_TOKEN, refreshToken, "");
        return fetchAccessToken(formData);
    }

    @Override
    public WhoAmI getWhoAmI(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ping/whoami").build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(WhoAmI.class)
                .block();
    }

    @Override
    public List<MonzoAccount> getAccounts(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts").build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoAccounts.class)
                .map(MonzoAccounts::getAccounts)
                .block();
    }

    @Override
    public MonzoPots getAllPots(String accessToken, String accountId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pots")
                        .queryParam("current_account_id", accountId)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoPots.class)
                .block();
    }

    @Override
    public MonzoAccount getBalanceForAccount(String accessToken, String accountId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/balance")
                        .queryParam("account_id", accountId)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoAccount.class)
                .block();
    }

    @Override
    public MonzoTransactionsResponse getTransactions(String accessToken, String accountId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transactions")
                        .queryParam("account_id", accountId)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoTransactionsResponse.class)
                .block();
    }

    private MultiValueMap<String, String> buildFormData(String grantType, String code, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String clientId = dotenv.get("MONZO_CLIENT_ID");
        String clientSecret = dotenv.get("MONZO_CLIENT_SECRET");
        formData.add("grant_type", grantType);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);

        formData.add(grantType.equals(REFRESH_TOKEN) ? "refresh_token" : "code", code);

        System.out.println(formData);
        return formData;
    }

    private MonzoAccessToken fetchAccessToken(MultiValueMap<String, String> formData) {
        try {
            MonzoAccessToken monzoAccessToken = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(HOST)
                            .path("/oauth2/token")
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(MonzoAccessToken.class)
                    .block();

            System.out.println("\n\n\n\n" + monzoAccessToken + "\n\n\n\n");
            return monzoAccessToken;
        } catch (WebClientException e) {
            throw new IllegalStateException("Failed to connect to Monzo authorization endpoint", e);
        }
    }
}

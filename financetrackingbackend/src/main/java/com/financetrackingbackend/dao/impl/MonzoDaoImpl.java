package com.financetrackingbackend.dao.impl;

import com.example.model.MonzoAccessToken;
import com.example.model.MonzoAccount;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransactionsResponse;
import com.example.model.WhoAmI;
import com.example.model.MonzoAccounts;
import com.financetrackingbackend.configuration.MonzoConfig;
import com.financetrackingbackend.dao.MonzoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Collections;
import java.util.List;

@Component
public class MonzoDaoImpl implements MonzoDao {
    private final String REFRESH_TOKEN = "refresh_token";
    private final String AUTHORISATION_CODE = "authorization_code";

    private final WebClient webClient;
    private final MonzoConfig monzoConfig;

    public MonzoDaoImpl(WebClient.Builder webClientBuilder, MonzoConfig monzoConfig) {
        this.webClient = webClientBuilder.baseUrl(monzoConfig.getBaseUrl()).build();
        this.monzoConfig = monzoConfig;
    }

    @Override
    public MonzoAccessToken exchangeAuthCode(String authCode) {
        if (StringUtils.isAnyBlank(authCode)) {
            throw new IllegalStateException("Required information for request is blank");
        }

        MultiValueMap<String, String> formData = buildFormData(AUTHORISATION_CODE, authCode, monzoConfig.getRedirectUrl());
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
        return requestHelper(accessToken, accessToken, "/ping/whoami", WhoAmI.class);
    }

    @Override
    public List<MonzoAccount> getAccounts(String accessToken) {
        MonzoAccounts accounts = requestHelper(accessToken, accessToken, "/accounts", MonzoAccounts.class);
        return accounts != null ? accounts.getAccounts() : Collections.emptyList();
    }

    @Override
    public MonzoPots getAllPots(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, "/pots", MonzoPots.class);
    }

    @Override
    public MonzoAccount getBalanceForAccount(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, "/balance", MonzoAccount.class);
    }

    @Override
    public MonzoTransactionsResponse getTransactions(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, "/transactions", MonzoTransactionsResponse.class);
    }

    private <T> T requestHelper(String accessToken, String accountId, String endpoint, Class<T> elementClass) {
        return webClient.get()
                .uri(endpoint + "?account_id=" + accountId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(elementClass)
                .block();
    }

    private MultiValueMap<String, String> buildFormData(String grantType, String code, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String clientId = monzoConfig.getClientId();
        String clientSecret = monzoConfig.getClientSecret();
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
                            .host(monzoConfig.getBaseUrl())
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

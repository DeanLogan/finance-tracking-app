package com.financetrackingbackend.dao.impl;

import com.example.model.MonzoAccessToken;
import com.example.model.MonzoAccount;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransactionsResponse;
import com.example.model.WhoAmI;
import com.example.model.MonzoAccounts;
import com.financetrackingbackend.configuration.MonzoConfig;
import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

import static com.financetrackingbackend.util.AppConstants.ACCOUNTS_PATH;
import static com.financetrackingbackend.util.AppConstants.ACCOUNT_ID;
import static com.financetrackingbackend.util.AppConstants.AUTH_CODE;
import static com.financetrackingbackend.util.AppConstants.AUTH_TOKEN_PATH;
import static com.financetrackingbackend.util.AppConstants.BALANCE_PATH;
import static com.financetrackingbackend.util.AppConstants.CLIENT_ID;
import static com.financetrackingbackend.util.AppConstants.CLIENT_SECRET;
import static com.financetrackingbackend.util.AppConstants.CODE;
import static com.financetrackingbackend.util.AppConstants.CURRENT_ACCOUNT_ID;
import static com.financetrackingbackend.util.AppConstants.EMPTY_STRING;
import static com.financetrackingbackend.util.AppConstants.EQUALS_SIGN;
import static com.financetrackingbackend.util.AppConstants.GRANT_TYPE;
import static com.financetrackingbackend.util.AppConstants.INCORRECT_TOKEN;
import static com.financetrackingbackend.util.AppConstants.MONZO_REQUEST_FAIL;
import static com.financetrackingbackend.util.AppConstants.MONZO_WHO_AM_I_PATH;
import static com.financetrackingbackend.util.AppConstants.POTS_PATH;
import static com.financetrackingbackend.util.AppConstants.QUESTION_MARK;
import static com.financetrackingbackend.util.AppConstants.REDIRECT_URI;
import static com.financetrackingbackend.util.AppConstants.REFRESH_TOKEN;
import static com.financetrackingbackend.util.AppConstants.REQUIRED_INFO_BLANK_ERROR_MSG;
import static com.financetrackingbackend.util.AppConstants.TRANSACTIONS_PATH;

@Component
public class MonzoDaoImpl implements MonzoDao {
    private final WebClient webClient;
    private final MonzoConfig monzoConfig;

    public MonzoDaoImpl(WebClient.Builder webClientBuilder, MonzoConfig monzoConfig) {
        this.webClient = webClientBuilder.baseUrl(monzoConfig.getBaseUrl()).build();
        this.monzoConfig = monzoConfig;
    }

    @Override
    public MonzoAccessToken exchangeAuthCode(String authCode) {
        if (StringUtils.isAnyBlank(authCode)) {
            throw new IllegalStateException(REQUIRED_INFO_BLANK_ERROR_MSG);
        }

        MultiValueMap<String, String> formData = buildFormData(AUTH_CODE, authCode, monzoConfig.getRedirectUrl());
        return fetchAccessToken(formData);
    }

    @Override
    public MonzoAccessToken refreshAccessToken(String refreshToken) {
        if (StringUtils.isAnyBlank(refreshToken)) {
            throw new IllegalStateException(REQUIRED_INFO_BLANK_ERROR_MSG);
        }

        MultiValueMap<String, String> formData = buildFormData(REFRESH_TOKEN, refreshToken, EMPTY_STRING);
        return fetchAccessToken(formData);
    }

    @Override
    public WhoAmI getWhoAmI(String accessToken) {
        return requestHelper(accessToken, EMPTY_STRING, EMPTY_STRING, MONZO_WHO_AM_I_PATH, WhoAmI.class);
    }

    @Override
    public List<MonzoAccount> getAccounts(String accessToken) {
        MonzoAccounts accounts = requestHelper(accessToken, EMPTY_STRING, EMPTY_STRING, ACCOUNTS_PATH, MonzoAccounts.class);
        return accounts != null ? accounts.getAccounts() : Collections.emptyList();
    }

    @Override
    public MonzoPots getAllPots(String accessToken, String accountId) {
        return requestHelper(accessToken, CURRENT_ACCOUNT_ID, accountId, POTS_PATH, MonzoPots.class);
    }

    @Override
    public MonzoAccount getBalanceForAccount(String accessToken, String accountId) {
        return requestHelper(accessToken, ACCOUNT_ID, accountId, BALANCE_PATH, MonzoAccount.class);
    }

    @Override
    public MonzoTransactionsResponse getTransactions(String accessToken, String accountId) {
        return requestHelper(accessToken, ACCOUNT_ID, accountId, TRANSACTIONS_PATH, MonzoTransactionsResponse.class);
    }

    private <T> T requestHelper(String accessToken, String queryParam, String paramValue, String endpoint, Class<T> elementClass) {
        try {
            return webClient.get()
                    .uri(endpoint + QUESTION_MARK + queryParam + EQUALS_SIGN + paramValue)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(elementClass)
                    .block();
        } catch(WebClientResponseException e) {
            throw new ServiceUnavailableException(MONZO_REQUEST_FAIL+e.getMessage());
        }
    }

    private MultiValueMap<String, String> buildFormData(String grantType, String code, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String clientId = monzoConfig.getClientId();
        String clientSecret = monzoConfig.getClientSecret();
        formData.add(GRANT_TYPE, grantType);
        formData.add(CLIENT_ID, clientId);
        formData.add(CLIENT_SECRET, clientSecret);
        formData.add(REDIRECT_URI, redirectUri);

        formData.add(grantType.equals(REFRESH_TOKEN) ? REFRESH_TOKEN : CODE, code);

        System.out.println(formData);
        return formData;
    }

    private MonzoAccessToken fetchAccessToken(MultiValueMap<String, String> formData) {
        try {
            return webClient.post()
                    .uri(AUTH_TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(MonzoAccessToken.class)
                    .block();
        } catch (WebClientResponseException e) {
            String message = MONZO_REQUEST_FAIL;
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                message = INCORRECT_TOKEN;
            }
            throw new ServiceUnavailableException(message+e.getMessage());
        } catch (WebClientException e) {
            throw new IllegalStateException(MONZO_REQUEST_FAIL, e);
        }
    }
}

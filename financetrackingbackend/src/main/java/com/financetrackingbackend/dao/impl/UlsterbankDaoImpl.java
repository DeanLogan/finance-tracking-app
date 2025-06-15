package com.financetrackingbackend.dao.impl;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankData;
import com.example.model.UlsterbankGeneralResponse;
import com.example.model.UlsterbankTransaction;
import com.example.model.UlsterbankBalance;
import com.example.model.UlsterbankAccount;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.exceptions.ServiceUnavailableException;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.financetrackingbackend.util.AppConstants.ACCOUNTS_SCOPE;
import static com.financetrackingbackend.util.AppConstants.ACCOUNT_ACCESS_CONSENTS_PATH;
import static com.financetrackingbackend.util.AppConstants.APPLICATION_JSON;
import static com.financetrackingbackend.util.AppConstants.AUTHORIZATION;
import static com.financetrackingbackend.util.AppConstants.AUTHORIZATION_CODE;
import static com.financetrackingbackend.util.AppConstants.BALANCES_ENDPOINT;
import static com.financetrackingbackend.util.AppConstants.BEARER;
import static com.financetrackingbackend.util.AppConstants.CLIENT_CREDENTIALS;
import static com.financetrackingbackend.util.AppConstants.CLIENT_ID;
import static com.financetrackingbackend.util.AppConstants.CLIENT_SECRET;
import static com.financetrackingbackend.util.AppConstants.CONTENT_TYPE;
import static com.financetrackingbackend.util.AppConstants.DATA;
import static com.financetrackingbackend.util.AppConstants.EMPTY_STRING;
import static com.financetrackingbackend.util.AppConstants.FORM_URLENCODED;
import static com.financetrackingbackend.util.AppConstants.GRANT_TYPE;
import static com.financetrackingbackend.util.AppConstants.GRANT_TYPE_ERROR;
import static com.financetrackingbackend.util.AppConstants.OPENID_ACCOUNTS_SCOPE;
import static com.financetrackingbackend.util.AppConstants.PERMISSIONS;
import static com.financetrackingbackend.util.AppConstants.PERMISSION_ARR;
import static com.financetrackingbackend.util.AppConstants.REFRESH_TOKEN;
import static com.financetrackingbackend.util.AppConstants.RISK;
import static com.financetrackingbackend.util.AppConstants.SCOPE;
import static com.financetrackingbackend.util.AppConstants.CODE;
import static com.financetrackingbackend.util.AppConstants.TOKEN_PATH;
import static com.financetrackingbackend.util.AppConstants.TRANSACTIONS_ENDPOINT;
import static com.financetrackingbackend.util.AppConstants.UB_ERROR_MSG;

@Component
public class UlsterbankDaoImpl implements UlsterbankDao {
    private final WebClient webClient;
    private final UlsterbankConfig config;

    public UlsterbankDaoImpl(WebClient.Builder webClientBuilder, UlsterbankConfig config) {
        this.webClient = webClientBuilder.baseUrl(config.getBaseUrl()).build();
        this.config = config;
    }

    @Override
    public UlsterbankAccessToken tokenRequest(String code, String grantType) {
        String clientId = config.getClientId();
        String clientSecret = config.getClientSecret();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CLIENT_ID, clientId);
        formData.add(CLIENT_SECRET, clientSecret);

        switch (grantType) {
            case CLIENT_CREDENTIALS:
                formData.add(SCOPE, ACCOUNTS_SCOPE);
                break;

            case AUTHORIZATION_CODE:
                formData.add(SCOPE, OPENID_ACCOUNTS_SCOPE);
                formData.add(CODE, code);
                break;

            case REFRESH_TOKEN:
                formData.add(REFRESH_TOKEN, code);
                break;

            default:
                throw new IllegalArgumentException(GRANT_TYPE_ERROR + grantType);
        }

        formData.add(GRANT_TYPE, grantType);

        try {
            return webClient.post()
                    .uri(TOKEN_PATH)
                    .header(CONTENT_TYPE, FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(UlsterbankAccessToken.class)
                    .block();
        } catch(WebClientResponseException e) {
            throw new ServiceUnavailableException(UB_ERROR_MSG + e.getMessage());
        }
    }

    @Override
    public UlsterbankGeneralResponse getConsentResponse(String accountRequestAccessToken) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put(PERMISSIONS, PERMISSION_ARR);
        requestBody.put(DATA, data);
        requestBody.put(RISK, new HashMap<>());

        try {
            return webClient.post()
                    .uri(ACCOUNT_ACCESS_CONSENTS_PATH)
                    .header(AUTHORIZATION, BEARER + accountRequestAccessToken)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(UlsterbankGeneralResponse.class)
                    .block();
        } catch(WebClientResponseException e) {
            throw new ServiceUnavailableException(UB_ERROR_MSG + e.getMessage());
        }
    }

    @Override
    public List<UlsterbankAccount> getAccounts(String accessToken) {
        return requestHelper(accessToken, EMPTY_STRING, EMPTY_STRING, UlsterbankData::getAccounts);
    }
    
    @Override
    public List<UlsterbankTransaction> getTransactions(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, TRANSACTIONS_ENDPOINT, UlsterbankData::getTransactions);
    }
    
    @Override
    public List<UlsterbankBalance> getBalances(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, BALANCES_ENDPOINT, UlsterbankData::getBalances);
    }
    
    private <T> T requestHelper(String accessToken, String accountId, String endpoint, Function<UlsterbankData, T> mapper) {
        try {
            return webClient.get()
                    .uri(config.getAccountsUrl() + "/" + accountId + endpoint)
                    .header(AUTHORIZATION, BEARER + accessToken)
                    .retrieve()
                    .bodyToMono(UlsterbankGeneralResponse.class)
                    .map(UlsterbankGeneralResponse::getData)
                    .map(mapper)
                    .block();
        } catch(WebClientResponseException e) {
            throw new ServiceUnavailableException(UB_ERROR_MSG + e.getMessage());
        }
    }
}
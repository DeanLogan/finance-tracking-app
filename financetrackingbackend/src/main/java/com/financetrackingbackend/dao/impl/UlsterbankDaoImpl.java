package com.financetrackingbackend.dao.impl;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankData;
import com.example.model.UlsterbankGeneralResponse;
import com.example.model.UlsterbankTransaction;
import com.example.model.UlsterbankBalance;
import com.example.model.UlsterbankAccount;
import com.financetrackingbackend.configuration.UlsterbankConfig;
import com.financetrackingbackend.dao.UlsterbankDao;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class UlsterbankDaoImpl implements UlsterbankDao {
    private final WebClient webClient;
    private final UlsterbankConfig config;
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    public UlsterbankDaoImpl(WebClient.Builder webClientBuilder, UlsterbankConfig config) {
        this.webClient = webClientBuilder.baseUrl(config.getBaseUrl()).build();
        this.config = config;
    }

    @Override
    public UlsterbankAccessToken tokenRequest(String code, String grantType) {
        String clientId = config.getClientId();
        String clientSecret = config.getClientSecret();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        switch (grantType) {
            case "client_credentials":
                formData.add("scope", "accounts");
                break;

            case "authorization_code":
                formData.add("scope", "openid accounts");
                formData.add("code", code);
                break;

            case "refresh_token":
                formData.add("refresh_token", code);
                break;

            default:
                throw new IllegalArgumentException("Unsupported grant type: " + grantType);
        }

        formData.add("grant_type", grantType);

        Mono<UlsterbankAccessToken> response = webClient.post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(UlsterbankAccessToken.class);

        return response.block();
    }

    @Override
    public UlsterbankGeneralResponse getConsentResponse(String accountRequestAccessToken) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("Permissions", Arrays.asList(
                "ReadAccountsDetail",
                "ReadBalances",
                "ReadTransactionsCredits",
                "ReadTransactionsDebits",
                "ReadTransactionsDetail"
        ));
        requestBody.put("Data", data);
        requestBody.put("Risk", new HashMap<>());

        return webClient.post()
                .uri("open-banking/v3.1/aisp/account-access-consents")
                .header(AUTHORIZATION, BEARER + accountRequestAccessToken)
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(UlsterbankGeneralResponse.class)
                .block();
    }

    @Override
    public List<UlsterbankAccount> getAccounts(String accessToken) {
        return requestHelper(accessToken, "", "", UlsterbankData::getAccounts);
    }
    
    @Override
    public List<UlsterbankTransaction> getTransactions(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, "/transactions", UlsterbankData::getTransactions);
    }
    
    @Override
    public List<UlsterbankBalance> getBalances(String accessToken, String accountId) {
        return requestHelper(accessToken, accountId, "/balances", UlsterbankData::getBalances);
    }
    
    private <T> T requestHelper(String accessToken, String accountId, String endpoint, Function<UlsterbankData, T> mapper) {
        return webClient.get()
                .uri(config.getAccountsUrl() + accountId + endpoint)
                .header(AUTHORIZATION, BEARER + accessToken)
                .retrieve()
                .bodyToMono(UlsterbankGeneralResponse.class)
                .map(UlsterbankGeneralResponse::getData)
                .map(mapper)
                .block();
    }
}

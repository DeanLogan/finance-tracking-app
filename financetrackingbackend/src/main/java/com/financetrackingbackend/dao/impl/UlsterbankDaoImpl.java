package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankBalance;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankGeneralResponse;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankTransaction;
import io.github.cdimascio.dotenv.Dotenv;
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

@Component
public class UlsterbankDaoImpl implements UlsterbankDao {
    private final WebClient webClient;
    private final Dotenv dotenv;
    private static final String BASE_URL = "https://ob.sandbox.ulsterbank.co.uk";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    public UlsterbankDaoImpl(WebClient.Builder webClientBuilder, Dotenv dotenv) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.dotenv = dotenv;
    }

    @Override
    public UlsterbankAccessToken tokenRequest(String code, String grantType) {
        String clientId = dotenv.get("ULSTER_BANK_CLIENT_ID");
        String clientSecret = dotenv.get("ULSTER_BANK_CLIENT_SECRET");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        switch (grantType) {
            case "client credentials":
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
    public UlsterbankConsentResponse getConsentResponse(String accountRequestAccessToken) {
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
                .bodyToMono(UlsterbankConsentResponse.class)
                .block();
    }

    @Override
    public List<UlsterbankAccount> getAccounts(String accessToken) {
        UlsterbankGeneralResponse response = webClient.get()
                .uri("open-banking/v3.1/aisp/accounts")
                .header(AUTHORIZATION, BEARER+ accessToken)
                .retrieve()
                .bodyToMono(UlsterbankGeneralResponse.class)
                .block();
        return response.getData().getAccounts();
    }

    @Override
    public List<UlsterbankBalance> getBalances(String accessToken, String accountId) {
        UlsterbankGeneralResponse response = webClient.get()
                .uri("open-banking/v3.1/aisp/accounts/"+accountId+"/balances")
                .header(AUTHORIZATION, BEARER+ accessToken)
                .retrieve()
                .bodyToMono(UlsterbankGeneralResponse.class)
                .block();
        return response.getData().getBalances();
    }

    @Override
    public List<UlsterbankTransaction> getTransactions(String accessToken, String accountId) {
        UlsterbankGeneralResponse response = webClient.get()
                .uri("open-banking/v3.1/aisp/accounts/"+accountId+"/transactions")
                .header(AUTHORIZATION, BEARER, accessToken)
                .retrieve()
                .bodyToMono(UlsterbankGeneralResponse.class)
                .block();
        return  response.getData().getTransactions();
    }
}

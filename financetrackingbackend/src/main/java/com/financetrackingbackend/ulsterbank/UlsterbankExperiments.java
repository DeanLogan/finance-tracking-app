package com.financetrackingbackend.ulsterbank;

import com.financetrackingbackend.ulsterbank.schema.UlsterbankConsentResponse;
import com.financetrackingbackend.ulsterbank.schema.UlsterbankAccessToken;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UlsterbankExperiments {
    private final WebClient webClient;
    private final Dotenv dotenv;

    public UlsterbankAccessToken getAccessToken() {
        String clientId = dotenv.get("ULSTER_BANK_CLIENT_ID");
        String clientSecret = dotenv.get("ULSTER_BANK_CLIENT_SECRET");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("scope", "accounts");

        Mono<UlsterbankAccessToken> response = webClient.post()
            .uri("/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(UlsterbankAccessToken.class);

        return response.block();
    }

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
            .header("Authorization", "Bearer " + accountRequestAccessToken)
            .header("Content-Type", "application/json")
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .bodyToMono(UlsterbankConsentResponse.class)
                .block();
    }

    public String extractConsentId(UlsterbankConsentResponse response) {
        return response.getData().getConsentId();
    }

    public String extractRedirectUrl(UlsterbankConsentResponse response) {
        return response.getLinks().getSelf();
    }
}

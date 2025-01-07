package com.financetrackingbackend.ulsterbank;

import com.financetrackingbackend.ulsterbank.schema.UlsterbankConsentResponse;
import com.financetrackingbackend.ulsterbank.schema.UlsterbankAccessToken;
import com.financetrackingbackend.ulsterbank.schema.UlsterbankData;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UlsterbankExperiments {
    private final WebClient webClient;
    private final Dotenv dotenv;

    public UlsterbankAccessToken tokenRequest(String code) {
        String clientId = dotenv.get("ULSTER_BANK_CLIENT_ID");
        String clientSecret = dotenv.get("ULSTER_BANK_CLIENT_SECRET");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        if(code.equalsIgnoreCase("code")) {
            System.out.println("client credentials");
            formData.add("grant_type", "client_credentials");
            formData.add("scope", "accounts");
        } else {
            System.out.println("access token");
            formData.add("grant_type", "authorization_code");
            formData.add("scope", "openid accounts");
            formData.add("code", code);
        }

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
        return tokenRequest(code);
    }
}

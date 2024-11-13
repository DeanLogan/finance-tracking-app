package com.financetrackingbackend.monzo;

import com.financetrackingbackend.monzo.schema.MonzoAccessToken;
import com.financetrackingbackend.monzo.schema.WhoAmI;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MonzoExperiments {
    private final WebClient webClient;
    private final Dotenv dotenv;
    private String totallySecureStateToken = null;
    private MonzoAccessToken monzoAccessToken;

    @Setter
    private String totallySecureAuthCode = null;

    public WhoAmI getWhoAmI() {
        if(monzoAccessToken == null) {
            buildMonzoAuthorizationUrl();
            return new WhoAmI(false, "1", "1");
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ping/whoami")
                        .queryParam("Authorization: Bearer", monzoAccessToken.getAccessToken())
                        .build())
                .headers(headers -> headers.setBearerAuth(dotenv.get("MONZO_ACCESS_TOKEN")))
                .retrieve()
                .bodyToMono(WhoAmI.class)
                .block();
    }

    public String buildMonzoAuthorizationUrl() {
        String clientId = dotenv.get("MONZO_CLIENT_ID");
        String redirectUri = dotenv.get("MONZO_REDIRECT_URI");

        if (!StringUtils.isNoneBlank(clientId, redirectUri)) {
            throw new IllegalStateException("Environment variables are not configured");
        }

        totallySecureStateToken = generateStateToken();
        if (totallySecureStateToken == null) {
            throw new IllegalStateException("Failed to generate state token");
        }

        try {
            ResponseEntity<String> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("auth.monzo.com")
                            .queryParam("client_id", clientId)
                            .queryParam("redirect_uri", redirectUri)
                            .queryParam("response_type", "code")
                            .queryParam("state", totallySecureStateToken)
                            .build())
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response == null) {
                throw new IllegalStateException("No response received from Monzo authorization endpoint");
            }

            System.out.println(response);
            return "success";

        } catch (WebClientException e) {
            throw new IllegalStateException("Failed to connect to Monzo authorization endpoint", e);
        }
    }

    private String generateStateToken() {
        try {
            byte[] randomBytes = new byte[32];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateStateToken(String givenStateToken){
        return (givenStateToken != null && givenStateToken.equals(totallySecureStateToken));
    }

    public void exchangeAuthCode() {
        String clientId = dotenv.get("MONZO_CLIENT_ID");
        String redirectUri = dotenv.get("MONZO_REDIRECT_URI");
        String clientSecret = dotenv.get("MONZO_CLIENT_SECRET");

        if (StringUtils.isAnyBlank(clientId, redirectUri, clientSecret)) {
            throw new IllegalStateException("Environment variables are not configured");
        }

        totallySecureStateToken = generateStateToken();
        if (totallySecureStateToken == null) {
            throw new IllegalStateException("Failed to generate state token");
        }

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);
            formData.add("code", totallySecureAuthCode);

            monzoAccessToken = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.monzo.com")
                            .path("/oauth2/token")
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(MonzoAccessToken.class)
                    .block();

        } catch (WebClientException e) {
            throw new IllegalStateException("Failed to connect to Monzo authorization endpoint", e);
        }
    }

}

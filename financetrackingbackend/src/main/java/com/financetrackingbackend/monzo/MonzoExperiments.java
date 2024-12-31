package com.financetrackingbackend.monzo;

import com.financetrackingbackend.monzo.schema.*;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MonzoExperiments {
    private final String REFRESH_TOKEN = "refresh_token";
    private final String AUTHORISATION_CODE = "authorization_code";
    private final String HOST = "api.monzo.com";

    private final WebClient webClient;
    private final Dotenv dotenv;
    private String totallySecureStateToken = null;
    private MonzoAccessToken monzoAccessToken;

    @Setter
    private String totallySecureAuthCode = null;

    public WhoAmI getWhoAmI(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ping/whoami").build())
                .headers(headers -> headers.setBearerAuth(accessToken))
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

        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("auth.monzo.com")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", totallySecureStateToken)
                .build()
                .toUriString();
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

    public MonzoAccessToken exchangeAuthCode() {
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

        return getMonzoAccessToken(AUTHORISATION_CODE, clientId, clientSecret, totallySecureAuthCode, redirectUri);
    }

    public MonzoAccessToken refreshAuthCode(String refreshToken) {
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

        return getMonzoAccessToken(REFRESH_TOKEN, clientId, clientSecret, refreshToken, "");
    }

    private MonzoAccessToken getMonzoAccessToken(String grantType, String clientId, String clientSecret, String authCode, String redirectUri) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", grantType);
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);

            String additionalFormData = "code";
            if(grantType.equals(REFRESH_TOKEN)) {
                additionalFormData = "refresh_token";
            }

            formData.add(additionalFormData, authCode);

            System.out.println(formData);

            monzoAccessToken = webClient.post()
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

            System.out.println("\n\n\n\n"+monzoAccessToken+"\n\n\n\n");

        } catch (WebClientException e) {
            throw new IllegalStateException("Failed to connect to Monzo authorization endpoint", e);
        }

        return monzoAccessToken;
    }

    private List<MonzoAccount> getAccounts(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts").build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoAccounts.class)
                .map(MonzoAccounts::getAccounts)
                .block();
    }

    private MonzoAccount getBalanceForAccount(String accessToken, MonzoAccount account) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/balance")
                        .queryParam("account_id", account.getId())
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MonzoAccount.class)
                .map(updatedFields -> {
                    if (updatedFields != null) {
                        account.setAccountBalance(updatedFields.getAccountBalance() / 100);
                        account.setTotalBalance(updatedFields.getTotalBalance() / 100);
                        account.setCurrency(updatedFields.getCurrency());
                        account.setSpendToday(updatedFields.getSpendToday() / 100);
                    }
                    return account;
                })
                .block();
    }

    public float getBalance(String accessToken) {
        List<MonzoAccount> accounts = getAccounts(accessToken);
        float balance = 0;
        if (accounts != null) {
            for (MonzoAccount account : accounts) {
                System.out.println("id being tested:"+account.getId());
                MonzoAccount monzoBalance = getBalanceForAccount(accessToken, account);
                System.out.println("accountTHing:"+monzoBalance);
                balance += monzoBalance.getAccountBalance();
            }
        }
        return balance;
    }

    private MonzoPots getAllPots(String accessToken, String accountId) {
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

    public MonzoPots getAllActivePots(String accessToken, String accountId) {
        AtomicReference<Float> totalBalance = new AtomicReference<>(0.0F);

        List<MonzoPot> activePots = getAllPots(accessToken, accountId).getPots()
                .stream()
                .filter(pot -> !pot.isDeleted())
                .peek(monzoPot -> {
                    float adjustedBalance = monzoPot.getBalance() / 100;
                    monzoPot.setBalance(adjustedBalance);
                    totalBalance.updateAndGet(currentTotal -> currentTotal + adjustedBalance);
                })
                .toList();

        MonzoPots monzoPots = new MonzoPots();
        monzoPots.setPots(activePots);
        monzoPots.setTotalPotsBalance(totalBalance.get());

        return monzoPots;
    }

    private MonzoAccount addActivePotsToAccount(String accessToken, MonzoAccount account) {
        account.setPots(getAllActivePots(accessToken, account.getId()));
        return account;
    }

    public MonzoUserInfoResponse getUserInfo(String accessToken) {
        MonzoUserInfoResponse response = new MonzoUserInfoResponse();
        List<MonzoAccount> accounts = getAccounts(accessToken);

        float totalBalance = 0.0F;

        for (int i = 0; i < accounts.size(); i++) {
            MonzoAccount account = accounts.get(i);
            account = getBalanceForAccount(accessToken, account);
            account = addActivePotsToAccount(accessToken, account);
            totalBalance += account.getTotalBalance();

            accounts.set(i, account);
        }
        response.setAccounts(accounts);
        response.setTotalBalance(totalBalance);

        return response;
    }
}

package com.financetrackingbackend.monzo;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

@Service
@RequiredArgsConstructor
public class MonzoExperiments {
    private final WebClient webClient;
    private final Dotenv dotenv;

    public String getLatestTransactionForAccount(String accountId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/transactions")
                        .queryParam("account_id", accountId)
                        .queryParam("limit", "1")
                        .build())
                .headers(headers -> headers.setBearerAuth(dotenv.get("MONZO_ACCESS_TOKEN")))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

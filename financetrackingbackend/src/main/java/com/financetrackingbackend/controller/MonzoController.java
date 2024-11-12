package com.financetrackingbackend.controller;

import com.financetrackingbackend.monzo.MonzoExperiments;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;


@RestController
@RequestMapping("/monzo")
@RequiredArgsConstructor
public class MonzoController {
    private final MonzoExperiments monzoExperiments;
    private final Dotenv dotenv;

    @GetMapping("/latestTransaction")
    public String getLatestTransaction() {
        return monzoExperiments.getLatestTransactionForAccount(dotenv.get("MONZO_ACCOUNT_ID"));
    }

    @GetMapping("/")
    public String home() {
        return "monzo";
    }
}

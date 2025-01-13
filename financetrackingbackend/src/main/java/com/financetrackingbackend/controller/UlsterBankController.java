package com.financetrackingbackend.controller;

import com.financetrackingbackend.ulsterbank.UlsterbankExperiments;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankData;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ulsterbank")
@RequiredArgsConstructor
public class UlsterBankController {
    private final Dotenv dotenv;
    private final UlsterbankExperiments ulsterbankExperiments;

    @GetMapping("")
    public String home() {
        return "ulsterbank";
    }

    @GetMapping("/auth")
    public String authoriseUser(@RequestHeader("code") String code) {
        return ulsterbankExperiments.tokenRequest(code, "client credentials").toString();
    }

    @GetMapping("/consentId")
    public List<String> getConsentId() {
        UlsterbankAccessToken accessToken = ulsterbankExperiments.tokenRequest("code", "client credentials");
        UlsterbankConsentResponse consent = ulsterbankExperiments.getConsentResponse(accessToken.getAccessToken());
        List<String> response = new ArrayList<>();
        response.add(ulsterbankExperiments.extractConsentId(consent));
        response.add(ulsterbankExperiments.extractRedirectUrl(consent));
        return response;
    }

    @GetMapping("/redirect")
    public String getRedirect() {
        String consent = getConsentId().get(0);
        return ulsterbankExperiments.getRedirectUrl(consent);
    }

    @GetMapping("/oauth/callback/extractcode")
    public UlsterbankAccessToken callback(@RequestHeader("code") String code, @RequestHeader("id_token") String idToken) {
        UlsterbankAccessToken accessToken = ulsterbankExperiments.getAccessToken(code);
        System.out.println("AccessToken:\n"+accessToken.getAccessToken());
        System.out.println("RefreshToken:\n"+accessToken.getRefreshToken());
        return accessToken;
    }

    @GetMapping("/oauth/refresh")
    public UlsterbankAccessToken refresh(@RequestHeader("refreshToken") String refreshToken) {
        UlsterbankAccessToken accessToken = ulsterbankExperiments.refreshAccessToken(refreshToken);
        System.out.println("AccessToken:\n"+accessToken.getAccessToken());
        System.out.println("RefreshToken:\n"+accessToken.getRefreshToken());
        return accessToken;
    }

    @GetMapping("/accounts")
    public UlsterbankData accounts(@RequestHeader("accessToken") String accessToken) {
        return ulsterbankExperiments.getAccounts(accessToken);
    }

    @GetMapping("/balance")
    public float balance(@RequestHeader("accessToken") String accessToken) {
        return ulsterbankExperiments.getBalanceForAllAccounts(accessToken);
    }
}

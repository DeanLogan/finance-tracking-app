package com.financetrackingbackend.controller;

import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;
import com.financetrackingbackend.ulsterbank.UlsterbankExperiments;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
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
    private final UlsterbankDao ulsterbankDao;
    private final UlsterbankExperiments ulsterbankExperiments;

    @GetMapping("")
    public String home() {
        return "ulsterbank";
    }

    @GetMapping("/auth")
    public String authoriseUser(@RequestHeader("code") String code) {
        return ulsterbankDao.tokenRequest(code, "client credentials").toString();
    }

    @GetMapping("/consentId")
    public List<String> getConsentId() {
        UlsterbankAccessToken accessToken = ulsterbankDao.tokenRequest("code", "client credentials");
        UlsterbankConsentResponse consent = ulsterbankDao.getConsentResponse(accessToken.getAccessToken());
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
    public List<UlsterbankAccount> accounts(@RequestHeader("accessToken") String accessToken) {
        return ulsterbankDao.getAccounts(accessToken);
    }

    @GetMapping("/balance")
    public float balance(@RequestHeader("accessToken") String accessToken) {
        return ulsterbankExperiments.getBalanceForAllAccounts(accessToken);
    }
}

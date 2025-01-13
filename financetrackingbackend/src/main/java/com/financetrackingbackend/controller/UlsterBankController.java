package com.financetrackingbackend.controller;

import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;
import com.financetrackingbackend.services.UlsterbankAccountService;
import com.financetrackingbackend.services.UlsterbankAuthService;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ulsterbank")
@RequiredArgsConstructor
public class UlsterBankController {
    private final UlsterbankDao ulsterbankDao;
    private final UlsterbankAccountService ulsterbankAccountService;
    private final UlsterbankAuthService ulsterbankAuthService;

    @GetMapping("")
    public String home() {
        return "ulsterbank";
    }

    @GetMapping("/auth")
    public String authoriseUser(@RequestHeader("code") String code) {
        return ulsterbankDao.tokenRequest(code, "client credentials").toString();
    }

    @GetMapping("/consentId")
    public String getConsentId() {
        UlsterbankAccessToken accessToken = ulsterbankDao.tokenRequest("code", "client credentials");
        UlsterbankConsentResponse consent = ulsterbankDao.getConsentResponse(accessToken.getAccessToken());
        return ulsterbankAuthService.extractConsentId(consent);
    }

    @GetMapping("/redirect")
    public String getRedirect() {
        return ulsterbankAuthService.getRedirectUrl(getConsentId());
    }

    @GetMapping("/oauth/callback/extractcode")
    public UlsterbankAccessToken callback(@RequestHeader("code") String code, @RequestHeader("id_token") String idToken) {
        UlsterbankAccessToken accessToken = ulsterbankAuthService.getAccessToken(code);
        System.out.println("AccessToken:\n"+accessToken.getAccessToken());
        System.out.println("RefreshToken:\n"+accessToken.getRefreshToken());
        return accessToken;
    }

    @GetMapping("/oauth/refresh")
    public UlsterbankAccessToken refresh(@RequestHeader("refreshToken") String refreshToken) {
        UlsterbankAccessToken accessToken = ulsterbankAuthService.refreshAccessToken(refreshToken);
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
        return ulsterbankAccountService.getBalanceForAllAccounts(accessToken);
    }
}

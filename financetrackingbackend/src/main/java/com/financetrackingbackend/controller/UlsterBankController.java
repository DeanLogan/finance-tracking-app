package com.financetrackingbackend.controller;

import com.example.api.UlsterbankApi;
import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankAccount;
import com.example.model.UlsterbankGeneralResponse;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.services.UlsterbankAccountService;
import com.financetrackingbackend.services.UlsterbankAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ulsterbank")
@RequiredArgsConstructor
public class UlsterBankController implements UlsterbankApi {
    private final UlsterbankDao ulsterbankDao;
    private final UlsterbankAccountService ulsterbankAccountService;
    private final UlsterbankAuthService ulsterbankAuthService;

    @GetMapping("/auth")
    public String authoriseUser(@RequestHeader("code") String code) {
        return ulsterbankDao.tokenRequest(code, "client_credentials").toString();
    }

    @GetMapping("/consentId")
    public ResponseEntity<String> getConsentId() {
        UlsterbankAccessToken accessToken = ulsterbankDao.tokenRequest("code", "client_credentials");
        UlsterbankGeneralResponse consent = ulsterbankDao.getConsentResponse(accessToken.getAccessToken());
        String consentId = ulsterbankAuthService.extractConsentId(consent);
        return ResponseEntity.ok(consentId);
    }
    
    @GetMapping("/redirect")
    public ResponseEntity<String> getRedirect() {
        String redirectUrl = ulsterbankAuthService.getRedirectUrl(getConsentId().getBody());
        return ResponseEntity.ok(redirectUrl);
    }
    
    @GetMapping("/oauth/callback/extractcode")
    public ResponseEntity<UlsterbankAccessToken> callback(@RequestHeader("code") String code, @RequestHeader("id_token") String idToken) {
        UlsterbankAccessToken accessToken = ulsterbankAuthService.getAccessToken(code);
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/oauth/refresh")
    public UlsterbankAccessToken refresh(@RequestHeader("refreshToken") String refreshToken) {
        return ulsterbankAuthService.refreshAccessToken(refreshToken);
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

package com.financetrackingbackend.controller;

import com.example.api.MonzoApi;
import com.example.model.MonzoAccessToken;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransaction;
import com.example.model.MonzoUserInfoResponse;
import com.example.model.WhoAmI;
import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.services.MonzoAccountService;
import com.financetrackingbackend.services.MonzoAuthService;
import com.financetrackingbackend.util.TokenUtil;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/monzo")
@RequiredArgsConstructor
public class MonzoController implements MonzoApi {
    private static final String STATE_TOKEN = "stateToken";

    private final MonzoAuthService monzoAuthService;
    private final Dotenv dotenv;
    private final MonzoDao monzoDao;
    private final MonzoAccountService monzoAccountService;

    @GetMapping("/auth")
    public String authoriseUser(HttpSession session) {
        String expectedStateToken = TokenUtil.generateStateToken();
        session.setAttribute(STATE_TOKEN, expectedStateToken);
        return "redirect: " + monzoAuthService.buildMonzoAuthUrl(expectedStateToken);
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> handleMonzoCallback(@RequestParam("code") String authCode, @RequestParam("state") String stateToken, HttpSession session) {
        String expectedStateToken = (String) session.getAttribute(STATE_TOKEN);
        if (!TokenUtil.validateStateToken(stateToken, expectedStateToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid state token");
        }

        session.removeAttribute(stateToken);

        MonzoAccessToken accessToken = monzoDao.exchangeAuthCode(authCode);
        return ResponseEntity.ok("AuthCode:\n" + authCode + "\n\nToken:\n" + accessToken);
    }

    @GetMapping("/oauth/refresh")
    public ResponseEntity<MonzoAccessToken> refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        MonzoAccessToken accessToken = monzoDao.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(accessToken);
    }
    
    @GetMapping("/whoami")
    public ResponseEntity<WhoAmI> getWhoAmI(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        WhoAmI whoAmI = monzoDao.getWhoAmI(accessToken);
        return ResponseEntity.ok(whoAmI);
    }
    
    @GetMapping("/balance")
    public ResponseEntity<Float> getAccounts(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Float balance = monzoAccountService.getBalanceForAllAccounts(accessToken);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/pots")
    public ResponseEntity<MonzoPots> getPots(@RequestHeader("accessToken") String accessToken, @RequestHeader("accountId") String accountId) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (accountId == null || accountId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        MonzoPots pots = monzoAccountService.getAllActivePotsForAccount(accessToken, accountId);
        return ResponseEntity.ok(pots);
    }
    
    @GetMapping("/userInfo")
    public ResponseEntity<MonzoUserInfoResponse> userInfo(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MonzoUserInfoResponse userInfo = monzoAccountService.getUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }
    
    @GetMapping("/transactions/list")
    public ResponseEntity<List<MonzoTransaction>> transactions(@RequestHeader("accessToken") String accessToken, @RequestHeader("accountId") String accountId) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (accountId == null || accountId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<MonzoTransaction> transactions = monzoAccountService.listTransactions(accessToken, accountId);
        return ResponseEntity.ok(transactions);
    }

    private String accessTokenEnvCheckTesting(String accessToken) {
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return accessToken;
    }
}

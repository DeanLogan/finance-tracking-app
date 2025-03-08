package com.financetrackingbackend.controller;

import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.schemas.monzo.MonzoAccessToken;
import com.financetrackingbackend.schemas.monzo.MonzoPots;
import com.financetrackingbackend.schemas.monzo.MonzoTransaction;
import com.financetrackingbackend.schemas.monzo.MonzoUserInfoResponse;
import com.financetrackingbackend.schemas.monzo.WhoAmI;
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
public class MonzoController {
    private static final String STATE_TOKEN = "stateToken";

    private final MonzoAuthService monzoAuthService;
    private final Dotenv dotenv;
    private final MonzoDao monzoDao;
    private final MonzoAccountService monzoAccountService;

    @GetMapping("")
    public String home() {
        return "monzo";
    }

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
    public MonzoAccessToken refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) {
        return monzoDao.refreshAccessToken(refreshToken);
    }

    @GetMapping("/whoami")
    public WhoAmI getWhoAmI(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        return monzoDao.getWhoAmI(accessToken);
    }

    @GetMapping("/balance")
    public float getAccounts(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        return monzoAccountService.getBalanceForAllAccounts(accessToken);
    }

    @GetMapping("/pots")
    public MonzoPots getPots(@RequestHeader("accessToken") String accessToken, @RequestParam("accountId") String accountId) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        return monzoAccountService.getAllActivePotsForAccount(accessToken, accountId);
    }

    @GetMapping("/userInfo")
    public MonzoUserInfoResponse userInfo(@RequestHeader("accessToken") String accessToken) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        return monzoAccountService.getUserInfo(accessToken);
    }

    @GetMapping("/transactions/list")
    public List<MonzoTransaction> transactions(@RequestHeader("accessToken") String accessToken, @RequestParam("accountId") String accountId) {
        accessToken = accessTokenEnvCheckTesting(accessToken);
        return monzoAccountService.listTransactions(accessToken, accountId);
    }

    private String accessTokenEnvCheckTesting(String accessToken) {
        log.info("access token=test:{}", accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return accessToken;
    }
}

package com.financetrackingbackend.controller;

import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.monzo.MonzoExperiments;
import com.financetrackingbackend.monzo.schema.MonzoAccessToken;
import com.financetrackingbackend.monzo.schema.MonzoPots;
import com.financetrackingbackend.monzo.schema.MonzoUserInfoResponse;
import com.financetrackingbackend.monzo.schema.WhoAmI;
import com.financetrackingbackend.services.MonzoAccountService;
import com.financetrackingbackend.util.TokenUtil;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/monzo")
@RequiredArgsConstructor
public class MonzoController {
    private final MonzoExperiments monzoExperiments;
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
        session.setAttribute("stateToken", expectedStateToken);
        String redirect = "redirect: " + monzoExperiments.buildMonzoAuthorizationUrl(expectedStateToken);
        System.out.println(redirect);
        return redirect;
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> handleMonzoCallback(@RequestParam("code") String authCode, @RequestParam("state") String stateToken, HttpSession session) {
        String expectedStateToken = (String) session.getAttribute("stateToken");
        
        if (!TokenUtil.validateStateToken(stateToken, expectedStateToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid state token");
        }

        session.removeAttribute("stateToken");

        String clientId = dotenv.get("MONZO_CLIENT_ID");
        String clientSecret = dotenv.get("");

        MonzoAccessToken accessToken = monzoDao.exchangeAuthCode(authCode);
        return ResponseEntity.ok("AuthCode:\n" + authCode + "\n\nToken:\n" + accessToken);
    }

    @GetMapping("/oauth/refresh")
    public MonzoAccessToken refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) {
        return monzoDao.refreshAccessToken(refreshToken);
    }

    @GetMapping("/whoami")
    public WhoAmI getWhoAmI(@RequestHeader("accessToken") String accessToken) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoDao.getWhoAmI(accessToken);
    }

    @GetMapping("/balance")
    public float getAccounts(@RequestHeader("accessToken") String accessToken) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoAccountService.getBalanceForAllAccounts(accessToken);
    }

    @GetMapping("/pots")
    public MonzoPots getPots(@RequestHeader("accessToken") String accessToken, @RequestParam("accountId") String accountId) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoAccountService.getAllActivePotsForAccount(accessToken, accountId);
    }

    @GetMapping("/userInfo")
    public MonzoUserInfoResponse userInfo(@RequestHeader("accessToken") String accessToken) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoAccountService.getUserInfo(accessToken);
    }
}

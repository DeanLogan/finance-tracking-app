package com.financetrackingbackend.controller;

import com.financetrackingbackend.monzo.MonzoExperiments;
import com.financetrackingbackend.monzo.schema.MonzoAccessToken;
import com.financetrackingbackend.monzo.schema.WhoAmI;
import io.github.cdimascio.dotenv.Dotenv;
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

    @GetMapping("/auth")
    public String authoriseUser() {
        String redirect = "redirect: "+monzoExperiments.buildMonzoAuthorizationUrl();
        System.out.println(redirect);
        return redirect;
    }

    @GetMapping("/whoami")
    public WhoAmI getWhoAmI(@RequestHeader("accessToken") String accessToken) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoExperiments.getWhoAmI(accessToken);
    }

    @GetMapping("/balance")
    public float getAccounts(@RequestHeader("accessToken") String accessToken) {
        System.out.println("access token=test:"+accessToken);
        if(accessToken == null || accessToken.equalsIgnoreCase("testing")) {
            accessToken = dotenv.get("MONZO_ACCESSTOKEN");
        }
        return monzoExperiments.getBalance(accessToken);
    }

    @GetMapping("")
    public String home() {
        return "monzo";
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> handleMonzoCallback(@RequestParam("code") String authorizationCode, @RequestParam("state") String stateToken) {
        if (!monzoExperiments.validateStateToken(stateToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid state token");
        }
        monzoExperiments.setTotallySecureAuthCode(authorizationCode);
        MonzoAccessToken accessToken = monzoExperiments.exchangeAuthCode();
        return ResponseEntity.ok("AuthCode:\n"+authorizationCode+"\n\nToken:\n"+accessToken);
    }

    @GetMapping("/oauth/refresh")
    public MonzoAccessToken refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) {
        return monzoExperiments.refreshAuthCode(refreshToken);
    }
}

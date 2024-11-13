package com.financetrackingbackend.controller;

import com.financetrackingbackend.monzo.MonzoExperiments;
import com.financetrackingbackend.monzo.schema.WhoAmI;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/monzo")
@RequiredArgsConstructor
public class MonzoController {
    private final MonzoExperiments monzoExperiments;
    private final Dotenv dotenv;

    @GetMapping("/whoami")
    public WhoAmI getWhoAmI() {
        return monzoExperiments.getWhoAmI();
    }

    @GetMapping("/")
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
        monzoExperiments.exchangeAuthCode();
        return ResponseEntity.ok("Authorization successful");
    }
}

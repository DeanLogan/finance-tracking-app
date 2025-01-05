package com.financetrackingbackend.controller;

import com.financetrackingbackend.ulsterbank.UlsterbankExperiments;
import com.financetrackingbackend.ulsterbank.schema.UlsterbankAccessToken;
import com.financetrackingbackend.ulsterbank.schema.UlsterbankConsentResponse;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String authoriseUser(HttpSession session) {
        return ulsterbankExperiments.getAccessToken().toString();
    }

    @GetMapping("/consentId")
    public List<String> getConsentId() {
        UlsterbankAccessToken accessToken = ulsterbankExperiments.getAccessToken();
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

    @GetMapping("/oauth/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("id_token") String idToken) {
        System.out.println("hit callback");
        System.out.println("code:\n" + code + "\n\nid:\n" + idToken);
        return "code:\n" + code + "\n\nid:\n" + idToken;
    }
}

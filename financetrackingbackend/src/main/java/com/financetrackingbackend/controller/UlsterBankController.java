package com.financetrackingbackend.controller;

import com.financetrackingbackend.ulsterbank.UlsterbankExperiments;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

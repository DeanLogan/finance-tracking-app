package com.financetrackingbackend.controller;

import com.example.api.TestApi;
import com.financetrackingbackend.configuration.ExperimentConfig;
import com.financetrackingbackend.exceptions.ResourceNotFoundException;
import com.financetrackingbackend.util.AuthenticationUtil;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ExperimentController implements TestApi {
    private final Dotenv dotenv;
    private final AuthenticationUtil authUtil;
    private final ExperimentConfig experimentConfig;

    @GetMapping("/")
    public String test() {
        return "hello world";
    }

    @GetMapping("/env")
    public String envFiles() {
        String secret = dotenv.get("SECRET"); 
        if (secret == null) {
            throw new ResourceNotFoundException("failed env check");
        }
        return secret;
    }

    @GetMapping("/config")
    public String configTest() {
        String testVal = experimentConfig.getValue();
        if (testVal == null) {
            throw new ResourceNotFoundException("failed to read config file");
        }
        return testVal;
    }

    @GetMapping("/whoami")
    public String whoAmI() {
        return authUtil.getCurrentUsername();
    }
}

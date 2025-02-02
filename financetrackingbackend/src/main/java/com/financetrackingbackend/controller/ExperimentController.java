package com.financetrackingbackend.controller;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ExperimentController {
    private final Dotenv dotenv;

    @GetMapping("/")
    public String test(){
        return "hello world";
    }

    @GetMapping("/envtest")
    public String envFiles() {
        String secret = dotenv.get("SECRET"); 
        if (secret == null) {
            return "failed env check";
        }
        return secret;
    }
}

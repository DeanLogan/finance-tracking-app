package com.financetrackingbackend.controller;

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
public class ExperimentController {
    private final Dotenv dotenv;
    private final AuthenticationUtil authUtil;

    @GetMapping("/")
    public String test(){
        return "hello world";
    }

    @GetMapping("/envtest")
    public String envFiles() {
        String secret = dotenv.get("SECRET"); 
        if (secret == null) {
            throw new ResourceNotFoundException("failed env check");
        }
        return secret;
    }

    @GetMapping("/whoami")
    public String whoami() {
        return authUtil.getCurrentUsername();
    }
}

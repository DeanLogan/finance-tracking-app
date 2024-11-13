package com.financetrackingbackend.controller;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return dotenv.get("SECRET");
    }
}

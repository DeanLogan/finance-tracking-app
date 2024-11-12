package com.financetrackingbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class FinanceTrackingController {
    @GetMapping("/test")
    public String test(){
        return "hello world";
    }

    @GetMapping("/monzo")
    public String monzoTest(){
        return "Testing monzo";
    }
}

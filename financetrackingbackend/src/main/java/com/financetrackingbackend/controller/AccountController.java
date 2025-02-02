package com.financetrackingbackend.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetrackingbackend.schemas.general.Account;
import com.financetrackingbackend.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private AccountService accountService;

    @GetMapping("/list/")
    public List<Account> list(){
        return accountService.listUserAccounts();
    }
}

package com.financetrackingbackend.controller;

import com.example.model.Account;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetrackingbackend.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/list")
    public List<Account> list(){
        return accountService.listUserAccounts();
    }

    @GetMapping("/get")
    public Account getAccount(@RequestParam("id") String id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/add")
    public Account add(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/update")
    public Account update(@RequestParam("id") String id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/delete")
    public Account delete(@RequestParam("id") String id) {
        return accountService.deleteAccount(id);
    }

    @GetMapping("/balance")
    public float balance() {
        return accountService.getBalanceForAllAccounts();
    }
}

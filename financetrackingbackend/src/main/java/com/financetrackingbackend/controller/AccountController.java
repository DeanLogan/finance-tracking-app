package com.financetrackingbackend.controller;

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

import com.financetrackingbackend.schemas.general.Account;
import com.financetrackingbackend.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private AccountService accountService;

    @GetMapping("/list")
    public List<Account> list(){
        return accountService.listUserAccounts();
    }

    @PostMapping("/addAccount")
    public Account add(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/updateAccount")
    public Account update(@RequestParam("id") int id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam("id") int id) {
        return accountService.deleteAccount(id);
    }

    @GetMapping("/balance")
    public float balance() {
        return accountService.getBalanceForAllAccounts();
    }
}

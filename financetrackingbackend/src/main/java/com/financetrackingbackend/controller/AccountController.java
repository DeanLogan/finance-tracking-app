package com.financetrackingbackend.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetrackingbackend.schemas.dynamodb.Account;
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

    @GetMapping("/getAccount")
    public ResponseEntity<Account> getAccount(@RequestParam("id") String id) {
        Account account = accountService.getAccount(id);
        return account == null ? ResponseEntity.status(404).body(null) : ResponseEntity.ok(account);
    }

    @PostMapping("/addAccount")
    public Account add(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/updateAccount")
    public Account update(@RequestParam("id") String id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam("id") String id) {
        return accountService.deleteAccount(id);
    }

    @GetMapping("/balance")
    public float balance() {
        return accountService.getBalanceForAllAccounts();
    }
}

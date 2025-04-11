package com.financetrackingbackend.services;

import com.example.model.Account;

import java.util.List;

public interface AccountService {
    List<Account> listUserAccounts();
    Account addAccount(Account account);
    float getBalanceForAllAccounts();
    Account deleteAccount(String id);
    Account updateAccount(String id, Account account);
    Account getAccount(String id);
}

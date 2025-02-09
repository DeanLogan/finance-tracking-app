package com.financetrackingbackend.services;

import java.util.List;

import com.financetrackingbackend.schemas.dynamodb.Account;

public interface AccountService {
    List<Account> listUserAccounts();
    Account addAccount(Account account);
    float getBalanceForAllAccounts();
    boolean deleteAccount(int id);
    Account updateAccount(int id, Account account);
}

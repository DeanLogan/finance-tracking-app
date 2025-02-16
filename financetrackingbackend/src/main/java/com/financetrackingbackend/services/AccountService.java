package com.financetrackingbackend.services;

import java.util.List;
import java.util.Map;

import com.financetrackingbackend.schemas.dynamodb.Account;

public interface AccountService {
    List<Account> listUserAccounts();
    Account addAccount(Account account);
    float getBalanceForAllAccounts();
    boolean deleteAccount(String id);
    Account updateAccount(String id, Account account);
    Account getAccount(String id);
}

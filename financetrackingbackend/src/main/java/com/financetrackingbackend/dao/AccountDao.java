package com.financetrackingbackend.dao;

import com.example.model.Account;

public interface AccountDao {
    Account getAccount(String id);
    Account addAccount(Account account);
    Account deleteAccount(String id);
    Account updateAccount(String id, Account account);
}

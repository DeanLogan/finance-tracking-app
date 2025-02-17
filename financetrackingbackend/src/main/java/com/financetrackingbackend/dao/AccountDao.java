package com.financetrackingbackend.dao;

import com.financetrackingbackend.schemas.dynamodb.Account;

import java.util.Map;

public interface AccountDao {
    Account getAccount(String id);
    Account addAccount(Account account);
    Account deleteAccount(String id);
    Account updateAccount(String id, Account account);
}

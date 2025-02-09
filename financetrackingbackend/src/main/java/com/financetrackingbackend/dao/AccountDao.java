package com.financetrackingbackend.dao;

import com.financetrackingbackend.schemas.general.Account;

import java.util.Map;

public interface AccountDao {
    Account getAccount(String id);
    void addAccount(Account account);
    void deleteAccount(String id);
    void updateAccount(String id, Map<String, Object> updatedFields);
}

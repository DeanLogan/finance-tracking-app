package com.financetrackingbackend.services;

import java.util.List;

import com.financetrackingbackend.schemas.general.Account;

public interface AccountService {
    List<Account> listUserAccounts();
    void addAccout(Account account);
    float getBalanceForAllAccounts();
}

package com.financetrackingbackend.services.impl;

import java.util.List;

import com.financetrackingbackend.schemas.general.Account;
import com.financetrackingbackend.services.AccountService;

public class AccountServiceImpl implements AccountService{

    @Override
    public List<Account> listUserAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'listUserAccounts'");
    }

    @Override
    public Account addAccount(Account account) {
        throw new UnsupportedOperationException("Unimplemented method 'addAccout'");
    }

    @Override
    public float getBalanceForAllAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'getBalanceForAllAccounts'");
    }

    @Override
    public boolean deleteAccount(int id) {
        return false;
    }

    @Override
    public Account updateAccount(int id, Account account) {
        return null;
    }

}

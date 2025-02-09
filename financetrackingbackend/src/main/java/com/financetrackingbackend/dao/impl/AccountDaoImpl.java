package com.financetrackingbackend.dao.impl;

import com.financetrackingbackend.dao.AccountDao;
import com.financetrackingbackend.schemas.general.Account;

import java.util.Map;

public class AccountDaoImpl implements AccountDao {
    @Override
    public Account getAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'getAccount'");
    }

    @Override
    public void addAccount(Account account) {
        throw new UnsupportedOperationException("Unimplemented method 'addAccount'");
    }

    @Override
    public void deleteAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
    }

    @Override
    public void updateAccount(String id, Map<String, Object> updatedFields) {
        throw new UnsupportedOperationException("Unimplemented method 'updateAccount'");
    }
}

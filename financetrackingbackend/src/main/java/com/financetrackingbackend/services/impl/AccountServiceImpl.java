package com.financetrackingbackend.services.impl;

import java.util.List;

import com.financetrackingbackend.dao.impl.AccountDaoImpl;
import com.financetrackingbackend.schemas.dynamodb.Account;
import com.financetrackingbackend.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountDaoImpl accountDao;

    @Override
    public List<Account> listUserAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'listUserAccounts'");
    }

    @Override
    public Account addAccount(Account account) {
        accountDao.addAccount(account);
        return null;
    }

    @Override
    public Account getAccount(String id) {
        return accountDao.getAccount(id);
    }

    @Override
    public float getBalanceForAllAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'getBalanceForAllAccounts'");
    }

    @Override
    public boolean deleteAccount(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
    }

    @Override
    public Account updateAccount(String id, Account account) {
        throw new UnsupportedOperationException("Unimplemented method 'updateAccount'");
    }

}

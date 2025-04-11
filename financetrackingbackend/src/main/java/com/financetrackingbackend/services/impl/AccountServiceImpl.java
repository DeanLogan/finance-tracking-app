package com.financetrackingbackend.services.impl;

import java.util.List;

import com.example.model.Account;
import com.financetrackingbackend.dao.impl.AccountDaoImpl;
import com.financetrackingbackend.exceptions.ResourceNotFoundException;
import com.financetrackingbackend.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountDaoImpl accountDao;
    private static final String ACC_NOT_FOUND_MSG = "Account was not found with id: ";

    @Override
    public List<Account> listUserAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'listUserAccounts'");
    }

    @Override
    public Account addAccount(Account account) {
        return accountDao.addAccount(account);
    }

    @Override
    public Account getAccount(String id) {
        Account account = accountDao.getAccount(id);
        if (account == null) {
            throw new ResourceNotFoundException(ACC_NOT_FOUND_MSG+id);
        }
        return account;
    }

    @Override
    public float getBalanceForAllAccounts() {
        throw new UnsupportedOperationException("Unimplemented method 'getBalanceForAllAccounts'");
    }

    @Override
    public Account deleteAccount(String id) {
        try {
            return accountDao.deleteAccount(id);
        } catch (ConditionalCheckFailedException e) {
            throw new ResourceNotFoundException(ACC_NOT_FOUND_MSG+id);
        }
    }

    @Override
    public Account updateAccount(String id, Account account) {
        try {
            return accountDao.updateAccount(id, account);
        } catch (ConditionalCheckFailedException e) {
            throw new ResourceNotFoundException(ACC_NOT_FOUND_MSG+id);
        }
    }

}

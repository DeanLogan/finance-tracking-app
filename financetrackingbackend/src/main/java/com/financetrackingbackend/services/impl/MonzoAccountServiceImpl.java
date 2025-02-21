package com.financetrackingbackend.services.impl;

import com.financetrackingbackend.dao.MonzoDao;
import com.financetrackingbackend.schemas.monzo.MonzoAccount;
import com.financetrackingbackend.schemas.monzo.MonzoPot;
import com.financetrackingbackend.schemas.monzo.MonzoPots;
import com.financetrackingbackend.schemas.monzo.MonzoUserInfoResponse;
import com.financetrackingbackend.services.MonzoAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonzoAccountServiceImpl implements MonzoAccountService {
    private final MonzoDao monzoDao;

    @Override
    public MonzoAccount getBalanceForAccount(String accessToken, MonzoAccount account) {
        MonzoAccount updatedFields = monzoDao.getBalanceForAccount(accessToken, account.getId());
        if (updatedFields != null) {
            account.setAccountBalance(updatedFields.getAccountBalance() / 100);
            account.setTotalBalance(updatedFields.getTotalBalance() / 100);
            account.setCurrency(updatedFields.getCurrency());
            account.setSpendToday(updatedFields.getSpendToday() / 100);
        }
        return account;
    }

    @Override
    public float getBalanceForAllAccounts(String accessToken) {
        List<MonzoAccount> accounts = monzoDao.getAccounts(accessToken);
        float balance = 0;
        if (accounts != null) {
            for (MonzoAccount account : accounts) {
                MonzoAccount monzoBalance = getBalanceForAccount(accessToken, account);
                balance += monzoBalance.getAccountBalance();
            }
        }
        return balance;
    }

    @Override
    public MonzoUserInfoResponse getUserInfo(String accessToken) {
        MonzoUserInfoResponse response = new MonzoUserInfoResponse();
        List<MonzoAccount> accounts = monzoDao.getAccounts(accessToken);

        float totalBalance = 0.0F;

        for (MonzoAccount account : accounts) {
            getBalanceForAccount(accessToken, account);
            addActivePotsToAccount(accessToken, account);
            totalBalance += account.getTotalBalance();
        }
        response.setAccounts(accounts);
        response.setTotalBalance(totalBalance);

        return response;
    }

    @Override
    public MonzoPots getAllActivePotsForAccount(String accessToken, String accountId) {
        AtomicReference<Float> totalBalance = new AtomicReference<>(0.0F);

        List<MonzoPot> activePots = monzoDao.getAllPots(accessToken, accountId).getPots()
                .stream()
                .filter(pot -> !pot.isDeleted())
                .map(monzoPot -> {
                    float adjustedBalance = monzoPot.getBalance() / 100;
                    monzoPot.setBalance(monzoPot.getBalance() / 100);
                    totalBalance.updateAndGet(currentTotal -> currentTotal + adjustedBalance);
                    return monzoPot;
                })
                .toList();

        MonzoPots monzoPots = new MonzoPots();
        monzoPots.setPots(activePots);
        monzoPots.setTotalPotsBalance(totalBalance.get());

        return monzoPots;
    }

    private void addActivePotsToAccount(String accessToken, MonzoAccount account) {
        account.setPots(getAllActivePotsForAccount(accessToken, account.getId()));
    }
}
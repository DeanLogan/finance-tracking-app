package com.financetrackingbackend.services.impl;

import com.example.model.UlsterbankAccount;
import com.example.model.UlsterbankBalance;
import com.financetrackingbackend.dao.UlsterbankDao;
import com.financetrackingbackend.services.UlsterbankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UlsterbankAccountServiceImpl implements UlsterbankAccountService {
    private final UlsterbankDao ulsterbankDao;

    @Override
    public float getBalanceForAllAccounts(String accessToken) {
        List<UlsterbankAccount> accountList = ulsterbankDao.getAccounts(accessToken);
        float amount = 0;
        for(UlsterbankAccount account : accountList) {
            UlsterbankBalance balance = getBalanceForAccount(accessToken, account.getAccountId());
            amount += balance.getAmount().getAmount();
        }
        return amount;
    }

    private UlsterbankBalance getBalanceForAccount(String accessToken, String accountId) {
        return ulsterbankDao.getBalances(accessToken, accountId).get(0);
    }
}

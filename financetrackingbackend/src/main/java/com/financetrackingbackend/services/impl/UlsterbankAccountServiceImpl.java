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
            amount += (float) getBalanceForAccount(accessToken, account.getAccountId());
        }
        return amount;
    }

    private double getBalanceForAccount(String accessToken, String accountId) {
        List<UlsterbankBalance> balances = ulsterbankDao.getBalances(accessToken, accountId);
        if(balances == null) {
            return 0;
        }
        return balances.stream()
                .filter(balance -> balance != null
                        && balance.getAmount() != null
                        && balance.getAmount().getAmount() != null)
                .mapToDouble(balance -> balance.getAmount().getAmount())
                .sum();
    }
}

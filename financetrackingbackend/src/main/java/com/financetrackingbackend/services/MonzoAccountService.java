package com.financetrackingbackend.services;

import com.financetrackingbackend.schemas.monzo.MonzoAccount;
import com.financetrackingbackend.schemas.monzo.MonzoPots;
import com.financetrackingbackend.schemas.monzo.MonzoTransaction;
import com.financetrackingbackend.schemas.monzo.MonzoUserInfoResponse;

import java.util.List;

public interface MonzoAccountService {
    MonzoAccount getBalanceForAccount(String accessToken, MonzoAccount account);
    float getBalanceForAllAccounts(String accessToken);
    MonzoUserInfoResponse getUserInfo(String accessToken);
    MonzoPots getAllActivePotsForAccount(String accessToken, String accountId);
    List<MonzoTransaction> listTransactions(String accessToken, String accountId);
}

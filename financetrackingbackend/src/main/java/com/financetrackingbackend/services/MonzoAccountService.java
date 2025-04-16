package com.financetrackingbackend.services;

import com.example.model.MonzoAccount;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransaction;
import com.example.model.MonzoUserInfoResponse;

import java.util.List;

public interface MonzoAccountService {
    MonzoAccount getBalanceForAccount(String accessToken, MonzoAccount account);
    float getBalanceForAllAccounts(String accessToken);
    MonzoUserInfoResponse getUserInfo(String accessToken);
    MonzoPots getAllActivePotsForAccount(String accessToken, String accountId);
    List<MonzoTransaction> listTransactions(String accessToken, String accountId);
}

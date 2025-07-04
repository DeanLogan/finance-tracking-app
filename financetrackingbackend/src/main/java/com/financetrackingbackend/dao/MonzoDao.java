package com.financetrackingbackend.dao;

import com.example.model.MonzoAccessToken;
import com.example.model.MonzoAccount;
import com.example.model.MonzoPots;
import com.example.model.MonzoTransactionsResponse;
import com.example.model.WhoAmI;

import java.util.List;

public interface MonzoDao {
    MonzoAccessToken exchangeAuthCode(String authCode);
    MonzoAccessToken refreshAccessToken(String refreshToken);
    WhoAmI getWhoAmI(String accessToken);
    List<MonzoAccount> getAccounts(String accessToken);
    MonzoPots getAllPots(String accessToken, String accountId);
    MonzoAccount getAccount(String accessToken, String accountId);
    MonzoTransactionsResponse getTransactions(String accessToken, String accountId);
}

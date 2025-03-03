package com.financetrackingbackend.dao;

import com.financetrackingbackend.schemas.monzo.MonzoAccessToken;
import com.financetrackingbackend.schemas.monzo.MonzoAccount;
import com.financetrackingbackend.schemas.monzo.MonzoPots;
import com.financetrackingbackend.schemas.monzo.MonzoTransactionsResponse;
import com.financetrackingbackend.schemas.monzo.WhoAmI;

import java.util.List;

public interface MonzoDao {
    MonzoAccessToken exchangeAuthCode(String authCode);
    MonzoAccessToken refreshAccessToken(String refreshToken);
    WhoAmI getWhoAmI(String accessToken);
    List<MonzoAccount> getAccounts(String accessToken);
    MonzoPots getAllPots(String accessToken, String accountId);
    MonzoAccount getBalanceForAccount(String accessToken, String accountId);
    MonzoTransactionsResponse getTransactions(String accessToken, String accountId);
}

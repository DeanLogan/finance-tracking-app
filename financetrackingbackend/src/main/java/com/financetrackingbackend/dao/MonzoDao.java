package com.financetrackingbackend.dao;

import com.financetrackingbackend.monzo.schema.MonzoAccessToken;
import com.financetrackingbackend.monzo.schema.MonzoAccount;
import com.financetrackingbackend.monzo.schema.MonzoPots;
import com.financetrackingbackend.monzo.schema.WhoAmI;

import java.util.List;


public interface MonzoDao {
    MonzoAccessToken exchangeAuthCode(String authCode);
    MonzoAccessToken refreshAccessToken(String refreshToken);
    WhoAmI getWhoAmI(String accessToken);
    List<MonzoAccount> getAccounts(String accessToken);
    MonzoPots getAllPots(String accessToken, String accountId);

}

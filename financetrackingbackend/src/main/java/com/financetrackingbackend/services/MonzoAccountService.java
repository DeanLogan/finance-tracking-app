package com.financetrackingbackend.services;

import com.financetrackingbackend.monzo.schema.MonzoAccount;
import com.financetrackingbackend.monzo.schema.MonzoPots;
import com.financetrackingbackend.monzo.schema.MonzoUserInfoResponse;

public interface MonzoAccountService {
    MonzoAccount getBalanceForAccount(String accessToken, MonzoAccount account);
    float getBalanceForAllAccounts(String accessToken);
    MonzoUserInfoResponse getUserInfo(String accessToken);
    MonzoPots getAllActivePotsForAccount(String accessToken, String accountId);
}

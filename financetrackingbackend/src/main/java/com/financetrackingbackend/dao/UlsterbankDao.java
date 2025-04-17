package com.financetrackingbackend.dao;

import com.example.model.UlsterbankAccessToken;
import com.example.model.UlsterbankAccount;
import com.example.model.UlsterbankBalance;
import com.example.model.UlsterbankGeneralResponse;
import com.example.model.UlsterbankTransaction;

import java.util.List;

public interface UlsterbankDao {
    UlsterbankAccessToken tokenRequest(String code, String grantTpye);
    UlsterbankGeneralResponse getConsentResponse(String accountRequestAccessToken);
    List<UlsterbankAccount> getAccounts(String accessToken);
    List<UlsterbankBalance> getBalances(String accessToken, String accountId);
    List<UlsterbankTransaction> getTransactions(String accessToken, String accountId);
}

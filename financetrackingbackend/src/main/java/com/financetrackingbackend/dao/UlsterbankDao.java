package com.financetrackingbackend.dao;

import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccessToken;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankBalance;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankConsentResponse;
import com.financetrackingbackend.schemas.ulsterbank.UlsterbankData;

import java.util.List;

public interface UlsterbankDao {
    UlsterbankAccessToken tokenRequest(String code, String grantTpye);
    UlsterbankConsentResponse getConsentResponse(String accountRequestAccessToken);
    List<UlsterbankAccount> getAccounts(String accessToken);
    List<UlsterbankBalance> getBalances(String accessToken, String accountId);
}

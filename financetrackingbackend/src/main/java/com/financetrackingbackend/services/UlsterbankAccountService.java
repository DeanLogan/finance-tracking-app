package com.financetrackingbackend.services;

import com.financetrackingbackend.schemas.ulsterbank.UlsterbankAccount;

import java.util.List;

public interface UlsterbankAccountService {
    float getBalanceForAllAccounts(String accessToken);
}

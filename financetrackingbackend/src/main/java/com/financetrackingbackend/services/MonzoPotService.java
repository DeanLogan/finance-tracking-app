package com.financetrackingbackend.services;

import com.financetrackingbackend.monzo.schema.MonzoPots;

public interface MonzoPotService {
    MonzoPots getAllActivePots(String accessToken, String accountId);
}

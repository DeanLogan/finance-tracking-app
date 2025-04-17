package com.financetrackingbackend.mapper;

import com.example.model.Account;

import java.util.Map;

public interface AccountMapper {
    Account map(Map<String, Object> map);
}

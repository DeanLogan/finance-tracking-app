package com.financetrackingbackend.mapper;

import com.financetrackingbackend.schemas.dynamodb.Account;
import java.util.Map;

public interface AccountMapper {
    Account map(Map<String, Object> map);
}

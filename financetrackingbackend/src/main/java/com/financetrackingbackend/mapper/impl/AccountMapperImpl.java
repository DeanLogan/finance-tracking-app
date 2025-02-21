package com.financetrackingbackend.mapper.impl;

import com.financetrackingbackend.mapper.AccountMapper;
import com.financetrackingbackend.schemas.dynamodb.Account;
import com.financetrackingbackend.schemas.dynamodb.Fee;
import com.financetrackingbackend.schemas.dynamodb.InterestRate;
import com.financetrackingbackend.schemas.dynamodb.Stock;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public Account map(Map<String, Object> map) {
        Account account = new Account();
        account.setId((String) map.get("id"));
        account.setName((String) map.get("name"));
        account.setBank((String) map.get("bank"));
        account.setAccountNumber(map.get("accountNumber") != null ? ((Number) map.get("accountNumber")).intValue() : null);
        account.setSortCode(map.get("sortCode") != null ? ((Number) map.get("sortCode")).intValue() : null);
        account.setBalance(map.get("balance") != null ? ((Number) map.get("balance")).floatValue() : null);
        account.setCurrentGains(map.get("currentGains") != null ? ((Number) map.get("currentGains")).floatValue() : null);
        account.setDateCreatedOn((String) map.get("dateCreatedOn"));
        account.setInterestRate(mapToInterestRate(map));
        account.setStocks(mapToStocks(map));
        account.setFees(mapToFees(map));
        return account;
    }

    private InterestRate mapToInterestRate(Map<String, Object> map) {
        Object obj = map.get("interestRate");
        if (obj instanceof Map) {
            Map<String, Object> interestMap = castToMap(obj);
            InterestRate rate = new InterestRate();
            rate.setAer(interestMap.get("aer") != null ? ((Number) interestMap.get("aer")).floatValue() : 0.0f);
            rate.setPaidTime((String) interestMap.get("paidTime"));
            rate.setStartDate((String) interestMap.get("startDate"));
            rate.setEndDate((String) interestMap.get("endDate"));
            return rate;
        }
        return null;
    }

    private List<Stock> mapToStocks(Map<String, Object> map) {
        Object obj = map.get("stocks");
        if (obj instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof Map)) {
                return list.stream()
                        .map(this::castToMap)
                        .map(stockMap -> {
                            Stock stock = new Stock();
                            stock.setName((String) stockMap.get("name"));
                            stock.setCurrentAmount(stockMap.get("currentAmount") != null ? ((Number) stockMap.get("currentAmount")).floatValue() : 0.0f);
                            stock.setGains(stockMap.get("gains") != null ? ((Number) stockMap.get("gains")).floatValue() : 0.0f);
                            stock.setTickerSymbol((String) stockMap.get("tickerSymbol"));
                            stock.setPurchaseDate((String) stockMap.get("purchaseDate"));
                            stock.setPurchasePrice((String) stockMap.get("purchasePrice"));
                            stock.setCurrentPrice((String) stockMap.get("currentPrice"));
                            return stock;
                        }).toList();
            }

        return Collections.emptyList();
    }

    private List<Fee> mapToFees(Map<String, Object> map) {
        Object obj = map.get("fees");
        if (obj instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof Map)) {
                return list.stream()
                        .map(this::castToMap)
                        .map(feeMap -> {
                            Fee fee = new Fee();
                            fee.setPercentage(feeMap.get("percentage") != null ? ((Number) feeMap.get("percentage")).floatValue() : 0.0f);
                            fee.setAmount(feeMap.get("amount") != null ? ((Number) feeMap.get("amount")).floatValue() : 0.0f);
                            return fee;
                        }).toList();
            }

        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        throw new IllegalArgumentException("Expected a Map but got: " + obj.getClass());
    }
}
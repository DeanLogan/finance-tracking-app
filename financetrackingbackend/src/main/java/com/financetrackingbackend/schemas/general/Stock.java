package com.financetrackingbackend.schemas.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Stock {
    @JsonProperty("name")
    private String name;
    @JsonProperty("currentAmount")
    private float currentAmount;
    @JsonProperty("currentGains")
    private float gains;
    @JsonProperty
    private Currency currency;
    @JsonProperty("tickerSymbol")
    private String tickerSymbol;
}

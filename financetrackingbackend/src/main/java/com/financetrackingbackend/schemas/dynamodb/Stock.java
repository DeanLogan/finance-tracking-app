package com.financetrackingbackend.schemas.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
public class Stock {
    @JsonProperty("name")
    private String name;
    @JsonProperty("currentAmount")
    private float currentAmount;
    @JsonProperty("gains")
    private float gains;
    @JsonProperty("currency")
    private Currency currency;
    @JsonProperty("tickerSymbol")
    private String tickerSymbol;
    @JsonProperty("purchaseDate")
    private String purchaseDate;
    @JsonProperty("purchasePrice")
    private String purchasePrice;
    @JsonProperty("currentPrice")
    private String currentPrice;
}

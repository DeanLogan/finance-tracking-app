package com.financetrackingbackend.schemas.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
public class InterestRate {
    @JsonProperty("aer")
    private float aer;
    @JsonProperty("accountType")
    private AccountType accountType;
    @JsonProperty("paidTime")
    private String paidTime;
}

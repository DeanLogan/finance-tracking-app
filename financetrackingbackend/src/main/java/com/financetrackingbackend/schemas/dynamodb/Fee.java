package com.financetrackingbackend.schemas.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.AccountType;
import com.financetrackingbackend.enums.PaymentRate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
public class Fee {
    @JsonProperty("type")
    private AccountType type;
    @JsonProperty("percentage")
    private float percentage;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("frequency")
    private PaymentRate frequency;
}

package com.financetrackingbackend.schemas.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InterestRate {
    @JsonProperty("aer")
    private float aer;
    @JsonProperty("accountType")
    private AccountType accountType;
    @JsonProperty("paidTime")
    private String paidTime;
}

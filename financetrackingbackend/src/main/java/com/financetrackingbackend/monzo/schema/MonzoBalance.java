package com.financetrackingbackend.monzo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoBalance {
    @JsonProperty("balance")
    private int balance;
    @JsonProperty("total_balance")
    private int totalBalance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("spend_today")
    private int spendToday;
}

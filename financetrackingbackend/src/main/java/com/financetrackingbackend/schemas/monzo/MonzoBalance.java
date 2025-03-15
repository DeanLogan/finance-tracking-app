package com.financetrackingbackend.schemas.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoBalance {
    @JsonProperty("balance")
    private float balance;
    @JsonProperty("total_balance")
    private float totalBalance;
    @JsonProperty("currency")
    private Currency currency;
    @JsonProperty("spend_today")
    private int spendToday;
}

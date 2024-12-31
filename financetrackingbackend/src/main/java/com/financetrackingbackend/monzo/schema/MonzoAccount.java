package com.financetrackingbackend.monzo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoAccount {
    @JsonProperty("id")
    private String id;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created")
    private String created;
    @JsonProperty("balance")
    private float accountBalance;
    @JsonProperty("total_balance")
    private float totalBalance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("spend_today")
    private int spendToday;
    @JsonProperty("pots")
    private MonzoPots pots;
}

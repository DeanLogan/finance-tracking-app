package com.financetrackingbackend.schemas.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoUserInfoResponse {
    @JsonProperty("total_balance")
    private float totalBalance;
    @JsonProperty("accounts")
    private List<MonzoAccount> accounts;
}
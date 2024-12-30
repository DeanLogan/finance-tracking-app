package com.financetrackingbackend.monzo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoAccounts {
    @JsonProperty("accounts")
    private List<MonzoAccount> accounts;
}

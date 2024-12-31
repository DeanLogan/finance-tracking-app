package com.financetrackingbackend.monzo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoPot {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("balance")
    private float balance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("deleted")
    private boolean deleted;
}

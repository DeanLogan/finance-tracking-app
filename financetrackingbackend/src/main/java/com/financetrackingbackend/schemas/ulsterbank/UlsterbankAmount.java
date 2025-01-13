package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankAmount {
    @JsonProperty("Amount")
    private float amount;
    @JsonProperty("Currency")
    private String currency;
}

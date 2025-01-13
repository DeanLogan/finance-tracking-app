package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterBankCreditLine {
    @JsonProperty("Included")
    private boolean included;
    @JsonProperty("Amount")
    private UlsterbankAmount amount;
    @JsonProperty("Type")
    private String type;
}

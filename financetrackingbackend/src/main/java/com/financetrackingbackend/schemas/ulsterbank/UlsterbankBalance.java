package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankBalance {
    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("Amount")
    private UlsterbankAmount amount;
    @JsonProperty("CreditDebitIndicator")
    private String creditDebitIndicator;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("DateTime")
    private String dateTime;
    @JsonProperty("CreditLine")
    private List<UlsterBankCreditLine> creditLine;
}

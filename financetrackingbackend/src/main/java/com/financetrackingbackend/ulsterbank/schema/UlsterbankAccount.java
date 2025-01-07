package com.financetrackingbackend.ulsterbank.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankAccount {
    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("AccountSubType")
    private String accountSubType;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Nickname")
    private String nickname;
    @JsonProperty("Account")
    private List<UlsterbankAccount> account;

    @JsonProperty("Servicer")
    private UlsterbankServicer servicer;
    @JsonProperty("SwitchStatus")
    private String switchStatus;

    @JsonProperty("SchemeName")
    private String schemeName;
    @JsonProperty("Identification")
    private String id;
    @JsonProperty("SecondaryIdentification")
    private String secondaryId;
    @JsonProperty("Name")
    private String name;
}

package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankData {
    @JsonProperty("ConsentId")
    private String consentId;
    @JsonProperty("CreationDateTime")
    private String creationDateTime;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("StatusUpdateDateTime")
    private String statusUpdateDateTime;
    @JsonProperty("Permissions")
    private List<String> permissions;
    @JsonProperty("Links")
    private UlsterbankLinks links;
    @JsonProperty("Meta")
    private UlsterbankMeta meta;
    @JsonProperty("Account")
    private List<UlsterbankAccount> accounts;
    @JsonProperty("Balance")
    private List<UlsterbankBalance> balances;
}
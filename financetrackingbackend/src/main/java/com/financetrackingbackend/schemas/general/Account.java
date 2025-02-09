package com.financetrackingbackend.schemas.general;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("bank")
    private String bank;
    @JsonProperty("accountNumber")
    private int accountNumber;
    @JsonProperty("sortCode")
    private int sortCode;
    @JsonProperty("currentAmount")
    private float currentAmount;
    @JsonProperty("stocks")
    private List<Stock> stocks;
    @JsonProperty("currentGains")
    private float currentGains;
    @JsonProperty("fees")
    private List<Fee> fees;
    @JsonProperty("dateCreatedOn")
    private String dateCreatedOn;
}

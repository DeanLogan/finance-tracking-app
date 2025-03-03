package com.financetrackingbackend.schemas.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonzoTransaction {
    @JsonProperty("id")
    private String id;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("created")
    private String createdAt;
    @JsonProperty("currency")
    private Currency currency;
    @JsonProperty("description")
    private String description;
    @JsonProperty("merchant")
    private String merchant;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("is_load")
    private Boolean isLoad;
    @JsonProperty("settled")
    private String settled;
    @JsonProperty("category")
    private String category;
}


package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UlsterbankLinks {
    @JsonProperty("Self")
    private String self;
    @JsonProperty("Prev")
    private String prev;
    @JsonProperty("Next")
    private String next;
}

package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankServicer {
    @JsonProperty("SchemeName")
    private String schemeName;
    @JsonProperty("Identification")
    private String id;
}

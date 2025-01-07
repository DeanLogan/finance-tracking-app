package com.financetrackingbackend.ulsterbank.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UlsterbankGeneralResponse {
    @JsonProperty("Data")
    private UlsterbankData data;
    @JsonProperty("Links")
    private UlsterbankLinks links;
    @JsonProperty("Meta")
    private UlsterbankMeta meta;
}

package com.financetrackingbackend.schemas.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonzoPots {
    @JsonProperty("total_pots_balance")
    private float totalPotsBalance;
    @JsonProperty("pots")
    private List<MonzoPot> pots;
}

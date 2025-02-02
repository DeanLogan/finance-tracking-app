package com.financetrackingbackend.schemas.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financetrackingbackend.enums.PaymentRate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Fee {
    @JsonProperty("fixedAmount")
    private float fixedFeeAmount;
    @JsonProperty("percentage")
    private float percentage;
    @JsonProperty("frequency")
    private PaymentRate frequency;
}

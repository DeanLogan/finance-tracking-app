package com.financetrackingbackend.schemas.ulsterbank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UlsterbankTransaction {
    @JsonProperty("AccountId")
    private String accountId;
    @JsonProperty("TransactionId")
    private String transactionId;
    @JsonProperty("CreditDebitIndicator")
    private boolean creditDebitIndicator;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("BookingDateTime")
    private String bookingDateTime;
    @JsonProperty("Amount")
    private UlsterbankAmount amount;
    @JsonProperty("TransactionInformation")
    private String transactionInformation;
    @JsonProperty("Balance")
    private UlsterbankBalance balance;
}

package com.financetrackingbackend.schemas.dynamodb;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
public class Account {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("bank")
    private String bank;
    @JsonProperty("accountNumber")
    private Integer accountNumber;
    @JsonProperty("sortCode")
    private Integer sortCode;
    @JsonProperty("balance")
    private Float balance;
    @JsonProperty("stocks")
    private List<Stock> stocks;
    @JsonProperty("currentGains")
    private Float currentGains;
    @JsonProperty("fees")
    private List<Fee> fees;
    @JsonProperty("dateCreatedOn")
    private String dateCreatedOn;
    @JsonProperty("interestRate")
    private InterestRate interestRate;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void generateIdIfMissing() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}


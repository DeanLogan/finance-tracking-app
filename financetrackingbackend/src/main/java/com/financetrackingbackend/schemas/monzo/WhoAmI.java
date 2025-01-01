package com.financetrackingbackend.schemas.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WhoAmI {
    @JsonProperty("authenticated")
    private boolean authenticated;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("user_id")
    private String userId;
}

package com.financetrackingbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.services.ulsterbank")
public class UlsterbankConfig {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String authUrl;
    private String accountsUrl;
}
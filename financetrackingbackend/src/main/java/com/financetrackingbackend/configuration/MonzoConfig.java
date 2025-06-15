package com.financetrackingbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.services.monzo")
public class MonzoConfig {
    private String baseUrl;
    private String authUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
}
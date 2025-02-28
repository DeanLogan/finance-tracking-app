package com.financetrackingbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.services")
public class ExternalServicesConfig {
    private String ulsterbankBaseUrl;
    private String ulsterbankClientId;
    private String ulsterbankClientSecret;
    private String ulsterbankAuthUrl;
    private String monzoBaseUrl;
    private String monzoRedirectUrl;
}
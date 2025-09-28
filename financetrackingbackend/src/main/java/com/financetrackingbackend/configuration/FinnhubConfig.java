package com.financetrackingbackend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.services.finnhub")
public class FinnhubConfig {
    private String baseUrl;
    private String token;
}

package com.financetrackingbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.dynamodb")
public class AwsConfig {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
}

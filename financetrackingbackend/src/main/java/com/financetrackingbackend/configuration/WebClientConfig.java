package com.financetrackingbackend.configuration;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .secure(sslContextSpec ->
                        {
                            try {
                                sslContextSpec.sslContext(SslContextBuilder.forClient()
                                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                        .build());
                            } catch (SSLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    )
            ))
            .baseUrl("https://ob.sandbox.ulsterbank.co.uk")
            .build();
    }
}

package com.bogaware.frugal.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("serp-api")
public class SerpApiConfiguration {
    private String apiUrl;
    private String apiKey;
    private String domain;
}

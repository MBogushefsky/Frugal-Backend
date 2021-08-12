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
@ConfigurationProperties("countdown")
public class CountdownConfiguration {
    private String apiUrl;
    private String apiKey;
    private String domain;
}

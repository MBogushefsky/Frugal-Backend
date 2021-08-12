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
@ConfigurationProperties("scraping")
public class WebScrapingConfiguration {
    private String userAgent;
    private String referrer;
    private int timeout;
    private boolean ignoreContentType;
    private boolean followRedirects;
}

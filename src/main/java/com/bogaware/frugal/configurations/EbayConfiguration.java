package com.bogaware.frugal.configurations;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("ebay")
public class EbayConfiguration extends HttpServlet {

    private String apiUrl;

    @Autowired
    ServletContext context;

    @Bean
    public OAuth2Api getEbayOAuth() throws FileNotFoundException {
        File configFile = new File(context.getRealPath("/WEB-INF/classes/ebay-config.yml"));
        if (configFile.exists()) {
            CredentialUtil.load(new FileInputStream(configFile.getAbsoluteFile()));
        }
        else {
            configFile = new File("src/main/resources/ebay-config.yml");
            CredentialUtil.load(new FileInputStream(configFile));
        }
        return new OAuth2Api();
    }
}

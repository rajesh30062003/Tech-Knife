package com.techknife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.github")
public class GitHubConfig {

    private String clientId;
    private String clientSecret;
    private String organization;
}

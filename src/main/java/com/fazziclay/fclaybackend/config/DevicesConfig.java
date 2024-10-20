package com.fazziclay.fclaybackend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("application.properties")
@ConfigurationProperties(prefix = "fclaybackend.person-status")
@Getter
@AllArgsConstructor
public class DevicesConfig {
    private Map<String, String> authorizationTokens;
}

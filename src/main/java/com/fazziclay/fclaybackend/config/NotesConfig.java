package com.fazziclay.fclaybackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
@ConfigurationProperties(prefix = "fclaybackend.notes")
@Getter
@Setter
public class NotesConfig {
    private String accessToken;
    private String noteFile;
}

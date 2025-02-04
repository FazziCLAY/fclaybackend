package com.fazziclay.fclaybackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Configuration
@ConfigurationProperties(prefix = "fclaybackend")
@Getter
@Setter
public class FclayConfig {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private String personStatusConfiguration;

    private transient PersonsStatusConfig personsStatus;

    @PostConstruct
    public void reloadConfigs() {
        Logger.debug("reloadConfigs();");
        personsStatus = getPersonsStatusConfig();
    }

    @SneakyThrows
    public PersonsStatusConfig getPersonsStatusConfig() {
        var file = getPersonStatusConfiguration();
        var path = Path.of(file);

        if (Files.notExists(path)) {
            String defaultConfigJson = OBJECT_MAPPER.writeValueAsString(PersonsStatusConfig.defaultConfig());
            Files.writeString(path, defaultConfigJson, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }

        return OBJECT_MAPPER.readValue(Files.readString(path), PersonsStatusConfig.class);
    }
}

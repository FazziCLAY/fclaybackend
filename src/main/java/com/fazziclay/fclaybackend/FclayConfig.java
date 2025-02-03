package com.fazziclay.fclaybackend;

import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Configuration
@ConfigurationProperties(prefix = "fclaybackend")
@Getter
@Setter
public class FclayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String personStatusConfiguration;
    private String notesConfiguration;
    private String adminTokenFile;
    private String authDbDir;

    private transient PersonsStatusConfig personsStatus;

    @PostConstruct
    public void reloadConfigs() {
        Logger.debug("reloadConfigs();");
        personsStatus = getPersonsStatusConfig();
    }

    @SneakyThrows
    public PersonsStatusConfig getPersonsStatusConfig() {
        var file = getPersonStatusConfiguration();
        var path = new File(file).toPath();

        if (Files.notExists(path)) {
            Files.writeString(path, GSON.toJson(PersonsStatusConfig.defaultConfig()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }

        return GSON.fromJson(Files.readString(path), PersonsStatusConfig.class);
    }
}

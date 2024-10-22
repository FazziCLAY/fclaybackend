package com.fazziclay.fclaybackend.admin;

import com.fazziclay.fclaybackend.FclayConfig;
import com.fazziclay.fclaybackend.notes.service.NotesService;
import com.fazziclay.fclaybackend.person.status.service.PersonsStatusService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AdminService {
    @Autowired
    private FclayConfig config;
    @Autowired
    private NotesService notesService;
    @Autowired
    private PersonsStatusService personsStatusService;
    @Autowired
    private ApplicationContext context;


    private String cachedAccessToken;
    private long tokenRefresh;



    private void checkAuth(String token) throws Exception {
        var atf = config.getAdminTokenFile();
        Path path = new File(atf).toPath();

        if (!Files.exists(path)) {
            Files.writeString(path, "ACCESS_TOKEN_HERE", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }

        if (cachedAccessToken == null || (System.currentTimeMillis() - tokenRefresh > 60*1000)) {
            cachedAccessToken = Files.readString(path).trim();
            tokenRefresh = System.currentTimeMillis();
        }

        if (cachedAccessToken.length() < 30) {
            throw new IllegalStateException("if server token < 30 => admin access disabled!");
        }

        if (!Objects.equals(token, cachedAccessToken)) {
            throw new Exception("No access grant");
        }
    }

    @SneakyThrows
    public String reload(String accessToken) {
        checkAuth(accessToken);

        config.reloadConfigs();
        notesService.reload(config.getNotesConfig());
        personsStatusService.reload(config.getPersonsStatusConfig());

        return "OK";
    }
}

package com.fazziclay.fclaybackend.admin;

import com.fazziclay.fclaybackend.FclayConfig;
import com.fazziclay.fclaybackend.auth.AuthService;
import com.fazziclay.fclaybackend.auth.misc.Permissions;
import com.fazziclay.fclaybackend.notes.service.NotesService;
import com.fazziclay.fclaybackend.person.status.service.PersonsStatusService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
    @Autowired
    private AuthService authService;


    private void checkAuth(String token) throws Exception {
        authService.authOrThrow(token, Permissions.ADMIN_RELOAD_CONFIGS);
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

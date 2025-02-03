package com.fazziclay.fclaybackend.admin;

import com.fazziclay.fclaybackend.FclayConfig;
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
    private PersonsStatusService personsStatusService;
    @Autowired
    private ApplicationContext context;

    @SneakyThrows
    public String reload(String accessToken) {
        if (true) {
            return "No impl yet;";
        }
        config.reloadConfigs();
        personsStatusService.reload(config.getPersonsStatusConfig());

        return "OK";
    }
}

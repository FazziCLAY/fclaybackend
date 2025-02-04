package com.fazziclay.fclaybackend.person.status.service;

import com.fazziclay.fclaybackend.Destroy;
import com.fazziclay.fclaybackend.FclayConfig;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@Setter
@Service
public class PersonsStatusService {
    private String defaultUser;
    private final HashMap<String, UserHandler> users = new HashMap<>();
    private final Timer timer = new Timer();

    @Autowired
    public PersonsStatusService(FclayConfig config) {
        reload(config.getPersonsStatusConfig());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (UserHandler user : users.values().toArray(new UserHandler[0])) {
                    user.tick();
                }
            }
        }, 0, 2000);
    }


    public void reload(PersonsStatusConfig config) {
        Destroy.destroyAll(users.values());
        users.clear();

        this.defaultUser = config.getDefaultUser();

        for (PersonsStatusConfig.PersonStatusUser cfgUser : config.getUsers()) {
            String name = cfgUser.getName();
            UserHandler e = new UserHandler(cfgUser);
            if (users.containsKey(name)) {
                throw new RuntimeException("Bad configuration. multiple users with equals names; name=" + name);
            }
            users.put(name, e);
        }

    }


    public UserHandler getUser(@Nullable String personName) {
        if (personName == null) {
            personName = defaultUser;
        }
        if (!users.containsKey(personName)) {
            throw new RuntimeException("No user found");
        }
        return users.get(personName);
    }


    // ============= API =================
    public MappingJacksonValue getActualPersonStatusFiltered(@Nullable String personName,
                                                             @Nullable String authorization,
                                                             @Nullable String fields) {
        return getUser(personName).getActualPersonStatusFiltered(authorization, fields);
    }

    public PersonStatus getActualPersonStatus(@Nullable String personName,
                                                     @Nullable String authorization) {
        return getUser(personName).getActualPersonStatus(authorization);
    }


    @SneakyThrows
    public PlaybackDto putHeadphones(@Nullable String personName,
                                     @Nullable String authorization,
                                     @Nullable PlaybackDto status) {
        return getUser(personName).putHeadphones(authorization, status);
    }


    @SneakyThrows
    public PlaybackDto patchHeadphones(@Nullable String personName,
                                       @Nullable String authorization,
                                       @NotNull PlaybackDto patch) {
        return getUser(personName).patchHeadphones(authorization, patch);
    }

    @SneakyThrows
    public PersonStatus patchPersonStatus(String personName, String authorization, Map<String, Object> updates) {
        return getUser(personName).patchPersonStatus(authorization, updates);
    }
}

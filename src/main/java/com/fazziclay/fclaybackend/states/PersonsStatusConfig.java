package com.fazziclay.fclaybackend.states;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PersonsStatusConfig {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String defaultUser = "name";
    private List<PersonStatusUser> users = new ArrayList<>();

    public static PersonsStatusConfig defaultConfig() {
        var r = new PersonsStatusConfig();

        PersonStatusUser e = new PersonStatusUser();
        PersonStatusDevice e2 = new PersonStatusDevice();
        e.devices.add(e2);

        ObjectNode e1 = OBJECT_MAPPER.createObjectNode();
        e1.put("service", "telegram_blog");
        e1.put("token", "bot token here");
        e1.put("channel", "@channel");
        e1.set("emojis", OBJECT_MAPPER.createObjectNode());
        e.autoPosts.add(e1);
        r.users.add(e);

        return r;
    }

    @Getter
    @Setter
    public static class PersonStatusUser {
        private String name = "name";
        @Nullable private String getAccessToken; // for GET values, nullable
        @Nullable private String modifyAccessToken; // for modifying values, nullable
        private List<PersonStatusDevice> devices = new ArrayList<>();
        private List<ObjectNode> autoPosts = new ArrayList<>();
    }


    @Getter
    @Setter
    public static class PersonStatusDevice {
        private String name = "device_name";
        private boolean isMobile = false;
        @NotNull private String accessToken = "";
        private long maxIdleAllowed = 1000*60;
    }
}

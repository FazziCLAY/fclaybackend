package com.fazziclay.fclaybackend.states;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PersonsStatusConfig {
    private String defaultUser = "name";
    private List<PersonStatusUser> users = new ArrayList<>();

    public static PersonsStatusConfig defaultConfig() {
        var r = new PersonsStatusConfig();

        PersonStatusUser e = new PersonStatusUser();
        PersonStatusDevice e2 = new PersonStatusDevice();
        e.devices.add(e2);

        JsonObject e1 = new JsonObject();
        e1.addProperty("service", "telegram_blog");
        e1.addProperty("token", "bot token here");
        e1.addProperty("channel", "@channel");
        e1.add("emojis", new JsonObject());
        e.autoPosts.add(e1);
        r.users.add(e);

        return r;
    }

    @Getter
    @Setter
    public static class PersonStatusUser {
        private String name = "name";
        @Nullable private String accessToken; // for GET values, must be null
        private List<PersonStatusDevice> devices = new ArrayList<>();
        private List<JsonObject> autoPosts = new ArrayList<>();
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

package com.fazziclay.fclaybackend.states;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotesConfig {
    private String directory = "./notes/";
    private List<NoteUser> users = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteUser {
        private String name;
        private String accessToken;
    }

    public static NotesConfig defaultConfig() {
        NotesConfig c = new NotesConfig();
        c.users.add(new NoteUser("", ""));
        return c;
    }
}

package com.fazziclay.fclaybackend.notes.service;

import com.fazziclay.fclaybackend.Destroy;
import com.fazziclay.fclaybackend.FclayConfig;
import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.notes.NoteDto;
import com.fazziclay.fclaybackend.states.NotesConfig;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotesService {
    private final List<NoteUser> users = new ArrayList<>();

    public NotesService(@Autowired FclayConfig config) throws Exception {
        reload(config.getNotesConfig());
    }

    public void reload(NotesConfig notesConfig) throws Exception {
        Destroy.emptyList(users);

        File notesDir = new File(notesConfig.getDirectory());
        if (!notesDir.exists()) {
            Files.createDirectories(notesDir.toPath());
        }

        for (NotesConfig.NoteUser user : notesConfig.getUsers()) {
            users.add(new NoteUser(notesDir, user));
        }
    }

    @NotNull
    private NoteUser getUserOrThrow(@Nullable String authorization) {
        return users.stream()
                .filter(noteUser -> noteUser.isAccessGrant(authorization))
                .findFirst()
                .orElseThrow(() -> new HttpException("No user found", HttpStatus.UNAUTHORIZED));
    }

    public NoteDto getNote(String authorization) {
        return getUserOrThrow(authorization).getNote();
    }

    @SneakyThrows
    public NoteDto setNote(String authorization, NoteDto note) {
        note.validate();
        return getUserOrThrow(authorization).setNote(note);
    }
}

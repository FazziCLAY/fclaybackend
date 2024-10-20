package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.config.NotesConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Service
public class NotesService {
    private final NotesConfig notesConfig;
    private final Path notePath;
    private String noteText;

    public NotesService(@Autowired NotesConfig notesConfig) throws IOException {
        this.notesConfig = notesConfig;
        this.notePath = new File(notesConfig.getNoteFile()).toPath();
        if (Files.notExists(notePath)) {
            Files.writeString(notePath, "Empty note\n\nText here\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
        this.noteText = Files.readString(notePath, StandardCharsets.UTF_8);
    }

    private void checkAuthorization(String authorization) {
        if (!notesConfig.getAccessToken().equals(authorization)) {
            throw new HttpException("No access.\n\nDear hacker, do not attempt see my private notes. If you are FBI => hack my VDS...", HttpStatus.UNAUTHORIZED);
        }
    }

    public String getNoteText(String authorization) {
        checkAuthorization(authorization);

        return noteText;
    }

    @SneakyThrows
    public String setNoteText(String authorization, String note) {
        checkAuthorization(authorization);
        this.noteText = note;
        // backup
        Path backupFile = Path.of("backupsNotes", System.currentTimeMillis() + ".txt");
        Files.createFile(backupFile);
        Files.copy(notePath, backupFile, StandardCopyOption.REPLACE_EXISTING);

        Files.deleteIfExists(notePath);
        Files.writeString(notePath, this.noteText, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        return noteText;
    }
}

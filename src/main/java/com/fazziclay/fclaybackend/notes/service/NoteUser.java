package com.fazziclay.fclaybackend.notes.service;

import com.fazziclay.fclaybackend.Destroy;
import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaybackend.notes.NoteCodec;
import com.fazziclay.fclaybackend.notes.NoteDto;
import com.fazziclay.fclaybackend.states.NotesConfig;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class NoteUser implements Destroy {
    private static final long MAX_LOCKED_MS = 6 * 1000;

    private final File noteDir;
    private final File upstreamFile;
    private final String accessToken;
    private final String name;

    private final NoteDto note;
    // <= 0 - no locked
    // other valued - millis on lock start
    private long lockedAt;

    public NoteUser(File notesDir, NotesConfig.NoteUser cfg) throws IOException {
        accessToken = cfg.getAccessToken();
        Objects.requireNonNull(accessToken);
        name = cfg.getName();
        Objects.requireNonNull(name);
        if (name.trim().isEmpty()) {
            throw new RuntimeException("Name of NoteUser can't be empty!");
        }
        this.noteDir = new File(notesDir, cfg.getName());
        Files.createDirectories(this.noteDir.toPath());

        upstreamFile = new File(this.noteDir, "upstream.txt");
        if (upstreamFile.exists()) {
            note = NoteCodec.CODEC.decode(Files.newInputStream(upstreamFile.toPath()));
            backup();

        } else {
            note = new NoteDto("Default note text", System.currentTimeMillis(), null);
            backup();
            save();
        }
        note.validate();
    }

    private void backup() {
        File backupFile = new File(noteDir, "backups/" + System.currentTimeMillis() + ".txt");
        save(backupFile);
    }

    private void save() {
        save(upstreamFile);
    }

    @SneakyThrows
    private void save(File file) {
        try {
            Files.createDirectories(file.getParentFile().toPath());
            file.createNewFile();
        } catch (IOException ignored) {}

        NoteCodec.CODEC.encode(note, Files.newOutputStream(file.toPath()));
    }

    public boolean isAccessGrant(@Nullable String authorization) {
        return Objects.equals(this.accessToken, authorization);
    }

    public NoteDto getNote() {
        return getNote(null);
    }

    public NoteDto getNote(@Nullable String[] specKeys) {
        val noteDto = new NoteDto(this.note.getText(), this.note.getLatestEdit(), (System.currentTimeMillis() - lockedAt < MAX_LOCKED_MS) ? 1 : null);
        if (specKeys != null) {
            noteDto.clearExclude(specKeys);
        }
        return noteDto;
    }

    public NoteDto setNote(NoteDto note) {
        note.validate();
        Objects.requireNonNull(note);
        Objects.requireNonNull(note.getText());
        if (note.getLatestEdit() != null) {
            Logger.debug("NoteUser::setNote received NoteDto with latestEdit. Ignored value.");
        }
        if (note.getL() != null) {
            Logger.debug("NoteUser::setNote received NoteDto with l. Ignored value.");
        }
        if (!Objects.equals(this.note.getText(), note.getText())) {
            backup();
            this.note.setText(note.getText());
            this.note.setLatestEdit(System.currentTimeMillis());
            this.lockedAt = 0; // unlock after changes received
            save();
        }
        return getNote();
    }

    @Override
    public void destroy() {
        // do nothing
    }

    public void lock() {
        lockedAt = System.currentTimeMillis();
    }
}

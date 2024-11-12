package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaybackend.auth.AuthService;
import com.fazziclay.fclaybackend.auth.db.TabItem;
import com.fazziclay.fclaybackend.notes.service.NotesService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NotesController {
    private NotesService notesService;
    private AuthService authService;

    @GetMapping("/tabs")
    public ResponseEntity<?> getTabs(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return handle(() -> authService.getNoteTabs(authorization));
    }

    @PostMapping("/tabs")
    public ResponseEntity<?> setTabs(@RequestHeader(value = "Authorization", required = false) String authorization, @RequestBody List<TabItem> tabsToPost) {
        return handle(() -> authService.setNoteTabs(authorization, tabsToPost));
    }

    @GetMapping
    public ResponseEntity<?> getNoteText(@RequestHeader(value = "Authorization", required = false) String authorization, @Nullable @RequestParam(value = "specKeys", required = false) String specKeysParam) {
        return handle(() -> {
            String[] specKeys = null;
            if (specKeysParam != null) {
                specKeys = specKeysParam.split(",");
            }
            return notesService.getNote(authorization, specKeys);
        });
    }

    @PatchMapping
    public ResponseEntity<?> setNoteText(@RequestHeader(value = "Authorization", required = false) String authorization, @RequestBody NoteDto note) {
        return handle(() -> notesService.setNote(authorization, note));
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lockNote(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Util.handleError(() -> {
            notesService.lockNote(authorization);
            return "";
        }, HttpStatus.NO_CONTENT);
    }

    private <T> ResponseEntity<?> handle(Supplier<T> supplier) {
        return Util.handleError(supplier, HttpStatus.OK);
    }
}


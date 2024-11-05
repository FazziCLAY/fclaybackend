package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaybackend.notes.service.NotesService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NotesController {
    private NotesService notesService;

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


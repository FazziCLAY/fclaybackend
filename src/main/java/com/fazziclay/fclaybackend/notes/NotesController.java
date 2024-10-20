package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.FclaySpringApplication;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<?> getNoteText(@RequestHeader("Authorization") String authorization) {
        return handle(() -> notesService.getNoteText(authorization), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> setNoteText(@RequestHeader("Authorization") String authorization, @RequestBody String note) {
        return handle(() -> notesService.setNoteText(authorization, note), HttpStatus.OK);
    }


    private <T> ResponseEntity<?> handle(Supplier<T> supplier, HttpStatus success) {
        return FclaySpringApplication.handle(supplier, success);
    }
}


package com.fazziclay.fclaybackend.notes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class NoteDto {
    private String text;
    private Long latestEdit;

    public void overwriteFrom(NoteDto noteDto) {
        this.text = noteDto.text;
        this.latestEdit = noteDto.latestEdit;
    }
}

package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.Logger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class NoteDto {
    @NotNull private String text;
    private Long latestEdit;

    public void validate() {
        Objects.requireNonNull(text);
        if (latestEdit != null) {
            if (latestEdit < 0) {
                throw new RuntimeException("latestEdit can't be negative");

            } else if (latestEdit > System.currentTimeMillis()) {
                latestEdit = System.currentTimeMillis();
                Logger.debug("latestEdit in future can't be allowed! sets to current time");
            }
        }
    }

    public void overwriteFrom(NoteDto noteDto) {
        this.text = noteDto.text;
        this.latestEdit = noteDto.latestEdit;
    }
}

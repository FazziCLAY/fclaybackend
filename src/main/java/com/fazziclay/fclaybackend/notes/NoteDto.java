package com.fazziclay.fclaybackend.notes;

import com.fazziclay.fclaybackend.Logger;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class NoteDto {
    private static final HashMap<String, Consumer<NoteDto>> KEYS_AND_CLEANERS = new HashMap<>();

    static {
        KEYS_AND_CLEANERS.put("text", noteDto -> noteDto.setText(null));
        KEYS_AND_CLEANERS.put("latestEdit", noteDto -> noteDto.setLatestEdit(null));
        KEYS_AND_CLEANERS.put("l", noteDto -> noteDto.setL(null));
    }

    private String text;
    private Long latestEdit;
    private Integer l;

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
        // here need editBy? it used only for response
    }

    public void clearExclude(String[] specKeys) {
        val keysToRemove = KEYS_AND_CLEANERS.keySet();
        List.of(specKeys).forEach(keysToRemove::remove);
        keysToRemove.forEach(s -> KEYS_AND_CLEANERS.get(s).accept(this));
    }
}

package com.fazziclay.fclaybackend.notes;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteCodec {
    public static NoteCodec CODEC = new NoteCodec();
    private final Gson GSON = new Gson();

    public NoteDto decode(InputStream is) {
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

        return GSON.fromJson(result, NoteDto.class);
    }

    @SneakyThrows
    public void encode(NoteDto note, OutputStream os) {
        String string = GSON.toJson(note, NoteDto.class);
        os.write(string.getBytes(StandardCharsets.UTF_8));
    }
}

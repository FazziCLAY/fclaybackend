package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class SongInfo {
    private String title;
    private String artist;
    private String album;

    public static SongInfo create(PlaybackDto p) {
        if (p == null) return null;
        return new SongInfo(p.getTitle(), p.getArtist(), p.getAlbum());
    }
}

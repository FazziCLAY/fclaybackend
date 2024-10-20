package com.fazziclay.fclaybackend.person.status;


import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UserHandler {
    private final String name;
    private final String accessToken;
    @Getter private final boolean mobile;
    @Getter private long latestUpdated;
    private PlaybackDto song;

    @Setter
    private long keepAliveRequiredMs = 60 * 1000;


    public boolean isAccess(String match) {
        return Objects.equals(match, accessToken);
    }

    public PlaybackDto getSong() {
        if (System.currentTimeMillis() - latestUpdated > keepAliveRequiredMs) {
            putHeadphones(null);
        }

        return song;
    }

    public UserHandler(String name, String accessToken, boolean mobile) {
        this.name = name;
        this.accessToken = accessToken;
        this.mobile = mobile;
    }

    public void putHeadphones(@Nullable PlaybackDto song) {
        this.latestUpdated = System.currentTimeMillis();
        this.song = song;
    }

    public boolean isActive() {
        return getSong() != null;
    }

    public void updatePersonStatus(PersonStatus status) {
        status.setOriginalHeadphones(song, latestUpdated);
        status.setIsMobile(mobile);
        status.setDeviceName(name);
    }
}

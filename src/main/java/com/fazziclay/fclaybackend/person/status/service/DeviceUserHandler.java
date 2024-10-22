package com.fazziclay.fclaybackend.person.status.service;


import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DeviceUserHandler {
    @Getter private final String name;
    private final String accessToken;
    @Getter private final boolean isMobile;
    @Getter private final long maxIdleAllowedMs;

    @Getter private long latestUpdated;
    private PlaybackDto song;

    public DeviceUserHandler(PersonsStatusConfig.PersonStatusDevice cfg) {
        this.name = cfg.getName();
        this.accessToken = cfg.getAccessToken();
        Objects.requireNonNull(this.accessToken, "accessToken can't be null!");
        if (this.accessToken.length() < 10) {
            throw new RuntimeException("accessToken length must be more than 9 chars");
        }
        this.isMobile = cfg.isMobile();
        this.maxIdleAllowedMs = cfg.getMaxIdleAllowed();
    }


    public boolean isAccessGrant(@Nullable String match) {
        return Objects.equals(match, accessToken);
    }

    public PlaybackDto getSong() {
        if (System.currentTimeMillis() - latestUpdated > maxIdleAllowedMs) {
            putHeadphones(null);
        }

        return song;
    }

    public DeviceUserHandler(String name, String accessToken, boolean isMobile, long maxIdleAllowedMs) {
        this.name = name;
        this.accessToken = accessToken;
        this.isMobile = isMobile;
        this.maxIdleAllowedMs = maxIdleAllowedMs;
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
        status.setIsMobile(isMobile);
        status.setDeviceName(name);
    }
}

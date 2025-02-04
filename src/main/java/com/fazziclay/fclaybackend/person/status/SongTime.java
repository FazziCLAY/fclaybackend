package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;

import java.util.Objects;

/**
 * For only one song, if plays another song => resets
 */
public class SongTime {
    private SongInfo latestSongInfo;
    private PlaybackDto latestSongPlayback;
    private long appendix;
    private long startListening;
    private long endListening;

    public void postSongInfo(PlaybackDto playbackDto) {
        var songInfo = SongInfo.create(latestSongPlayback = playbackDto);
        if (songInfo == null) {
            if (!isPaused() && latestSongInfo != null) {
                endListening = System.currentTimeMillis();
            }
            return;
        }

        if (!Objects.equals(latestSongInfo, songInfo)) {
            latestSongInfo = songInfo;
            appendix = 0;
            startListening = System.currentTimeMillis();
            endListening = 0;
            return;
        }

        if (isPaused()) {
            appendix = appendix + (endListening - startListening);
            startListening = System.currentTimeMillis();
            endListening = 0;
        }
    }

    public long getListeningDuration() {
        long end = endListening > 0 ? endListening : System.currentTimeMillis();
        return appendix + (end - startListening);
    }

    public boolean isPresent() {
        return latestSongInfo != null;
    }

    public float getPlaysCount() {
        if (!isPresent()) {
            return -1;
        }
        var songDuration = latestSongPlayback.getDuration();
        if (songDuration == null) return -1;
        var listeningDur = getListeningDuration();

        return ((float) listeningDur / (float) songDuration);
    }

    public boolean isPaused() {
        return endListening > 0;
    }
}

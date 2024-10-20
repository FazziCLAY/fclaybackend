package com.fazziclay.fclaybackend.person.status.autopost;

import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.SongInfo;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AutoPostFilter implements IAutoPost {
    private final Settings settings;

    private SongInfo chatHead = null;
    private long chatHeadActualAt;

    private SongInfo chatHeadWouldBe = null;
    private long chatHeadWouldBeSetAt;

    public AutoPostFilter(Settings settings) {
        this.settings = settings;
    }

    public AutoPostFilter() {
        this.settings = Settings.builder().build();
    }

    /**
     * Post to channel sound
     */
    @Override
    public void postPersonStatus(@NotNull PersonStatus status) {
        PlaybackDto headphones = status.getHeadphones();
        SongInfo songInfo = SongInfo.create(headphones);

        if (Objects.equals(songInfo, chatHead)) {
            chatHeadActualAt = System.currentTimeMillis();

        } else {
            if (System.currentTimeMillis() - chatHeadActualAt > settings.rateLimit && (System.currentTimeMillis() - chatHeadWouldBeSetAt > settings.changesApplyDelay)) {
                sendMessageAbout(status);
                chatHead = songInfo;
                chatHeadActualAt = System.currentTimeMillis();

            } else {
                if (!Objects.equals(chatHeadWouldBe, songInfo)) {
                    chatHeadWouldBe = songInfo;
                    chatHeadWouldBeSetAt = System.currentTimeMillis();
                }
            }

        }
    }

    public abstract void sendMessageAbout(PersonStatus status);

    @Builder()
    @Getter
    public static class Settings {
        @Builder.Default
        private long rateLimit = 5000;

        @Builder.Default
        private long changesApplyDelay = 3000;
    }

}

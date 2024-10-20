package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.config.DevicesConfig;
import com.fazziclay.fclaybackend.config.TelegramBotConfig;
import com.fazziclay.fclaybackend.person.status.autopost.IAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.StatisticAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.ovk.OVKApiAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.telegram.TelegramBotAutoPost;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Setter
@Service
@AllArgsConstructor
public class PersonStatusService {
    private final Statistic statistic = new Statistic();
    private final PersonStatus status = new PersonStatus();
    private final List<UserHandler> users = new ArrayList<>();
    private final Timer timer = new Timer();
    private final List<IAutoPost> autoPosts = new ArrayList<>();
    private long statusLatestUpdated;
    private TelegramBotConfig telegramBotConfig;

    @Autowired
    public PersonStatusService(DevicesConfig devicesConfig, TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
        this.autoPosts.add(new TelegramBotAutoPost(telegramBotConfig));
        this.autoPosts.add(new OVKApiAutoPost("ovkisser.fun", telegramBotConfig.getOvkisserToken()));
        this.autoPosts.add(new StatisticAutoPost(statistic));

        devicesConfig.getAuthorizationTokens().forEach((name, token) -> {
            UserHandler user = new UserHandler(name, token, name.startsWith("mobile"));
            if (user.isMobile()) {
                user.setKeepAliveRequiredMs(1000*60*10);
            }
            users.add(user);
        });
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - statusLatestUpdated > 1900) {
                    updateStatus();
                }
            }
        }, 0, 2000);
    }


    private void onUserUpdated(UserHandler handler) {
        updateStatus();
    }

    public Optional<UserHandler> getUserHandler(String authorization) {
        return users.stream()
                .filter(user -> user.isAccess(authorization))
                .findFirst();
    }

    @NotNull
    private Optional<UserHandler> calcActiveUser() {
        return users.stream()
                .filter(Objects::nonNull)
                .filter(UserHandler::isActive)
                .findFirst();
    }

    /**
     * Calc current and set to api status
     */
    private void updateStatus() {
        Optional<UserHandler> activeUser = calcActiveUser();
        if (activeUser.isEmpty()) {
            status.clear();

        } else {
            activeUser.get().updatePersonStatus(status);
        }
        autoPostsUpdate();
        statusLatestUpdated = System.currentTimeMillis();
    }


    /**
     * Post song to all IAutoPost's
     * anti-spam must be implemented in IAutoPost
     */
    private void autoPostsUpdate() {
        for (IAutoPost autoPost : autoPosts) {
            autoPost.postPersonStatus(status);
        }
    }

    // ============= API =================

    /**
     * Called from REST API
     */
    public PersonStatus getActualPersonStatus() {
        status.actualizePersonStatus();
        return status;
    }

    /**
     * Called from REST API
     */
    @SneakyThrows
    public PlaybackDto putHeadphones(String authorization, @Nullable PlaybackDto status) {
        var handler = getUserHandler(authorization).orElseThrow(() -> new HttpException("userHandler for provided accessToken not exists!", HttpStatus.UNAUTHORIZED));
        handler.putHeadphones(status);
        onUserUpdated(handler);

        return handler.getSong();
    }

    /**
     * Called from REST API
     */
    @SneakyThrows
    public PlaybackDto patchHeadphones(String authorization, @NotNull PlaybackDto patch) {
        var handler = getUserHandler(authorization).orElseThrow(() -> new HttpException("userHandler for provided accessToken not exists!", HttpStatus.UNAUTHORIZED));

        PlaybackDto patchedSong = handler.getSong().newWithPatch(patch);

        handler.putHeadphones(patchedSong);
        onUserUpdated(handler);

        return handler.getSong();
    }
}

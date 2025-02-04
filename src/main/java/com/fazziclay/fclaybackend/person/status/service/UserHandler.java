package com.fazziclay.fclaybackend.person.status.service;

import com.fazziclay.fclaybackend.Destroy;
import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.Statistic;
import com.fazziclay.fclaybackend.person.status.autopost.IAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.StatisticAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.ovk.OVKApiAutoPost;
import com.fazziclay.fclaybackend.person.status.autopost.telegram.TelegramBotAutoPost;
import com.fazziclay.fclaybackend.states.PersonsStatusConfig;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserHandler implements Destroy {
    private final String accessToken;
    private final Statistic statistic = new Statistic();
    private final PersonStatus status = new PersonStatus();
    private final List<IAutoPost> autoPosts = new ArrayList<>();
    private long statusLatestUpdated;
    private final List<DeviceUserHandler> devices = new ArrayList<>();

    public UserHandler(PersonsStatusConfig.PersonStatusUser cfgUser) {
        this.accessToken = cfgUser.getAccessToken();

        for (JsonObject obj : cfgUser.getAutoPosts()) {
            var service = obj.get("service").getAsString();

            switch (service) {
                case "telegram_blog" ->
                        this.autoPosts.add(new TelegramBotAutoPost(obj.get("token").getAsString(), obj.get("channel").getAsString(), obj.get("emojis").getAsJsonObject(), obj.get("silenceMsg")));
                case "ovk_status" ->
                        this.autoPosts.add(new OVKApiAutoPost(obj.get("host").getAsString(), obj.get("token").getAsString()));
                case "fclaystatistic" ->
                        this.autoPosts.add(new StatisticAutoPost(statistic));
            }
        }

        for (PersonsStatusConfig.PersonStatusDevice device : cfgUser.getDevices()) {
            DeviceUserHandler user = new DeviceUserHandler(device);
            devices.add(user);
        }
    }

    public void tick() {
        if (System.currentTimeMillis() - statusLatestUpdated > 1900) {
            updateStatus();
        }
    }

    private void onDeviceUpdated(DeviceUserHandler handler) {
        updateStatus();
    }

    public Optional<DeviceUserHandler> getDeviceHandler(@Nullable String authorization) {
        return devices.stream()
                .filter(user -> user.isAccessGrant(authorization))
                .findFirst();
    }

    private Optional<DeviceUserHandler> calcActiveDevice() {
        return devices.stream()
                .filter(Objects::nonNull)
                .filter(DeviceUserHandler::isActive)
                .findFirst();
    }

    /**
     * Calc current and set to api status
     */
    private void updateStatus() {
        Optional<DeviceUserHandler> activeDevice = calcActiveDevice();
        if (activeDevice.isEmpty()) {
            status.clear();

        } else {
            activeDevice.get().updatePersonStatus(status);
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

    public PersonStatus getActualPersonStatus(@Nullable String authorization) {
        checkGetPermission(authorization);
        status.actualize();
        return status;
    }

    // get
    public Statistic getStatistic(@Nullable String authorization) {
        checkGetPermission(authorization);
        return statistic;
    }

    // get
    private void checkGetPermission(@Nullable String authorization) {
        if (this.accessToken != null) {
            if (!Objects.equals(this.accessToken, authorization)) {
                throw new HttpException(HttpStatus.UNAUTHORIZED, "This user need authentication for get.");
            }
        }
    }

    // device
    public PlaybackDto putHeadphones(@Nullable String authorization, PlaybackDto status) {
        var handler = getDeviceHandler(authorization).orElseThrow(() -> new HttpException(HttpStatus.UNAUTHORIZED, "no device found"));
        handler.putHeadphones(status);
        onDeviceUpdated(handler);

        return handler.getSong();
    }

    // device
    public PlaybackDto patchHeadphones(String authorization, PlaybackDto patch) {
        var handler = getDeviceHandler(authorization).orElseThrow(() -> new HttpException(HttpStatus.UNAUTHORIZED, "no device found"));

        PlaybackDto patchedSong = handler.getSong().newWithPatch(patch);

        handler.putHeadphones(patchedSong);
        onDeviceUpdated(handler);

        return handler.getSong();
    }

    @Override
    public void destroy() {
        // dp nothing
    }
}

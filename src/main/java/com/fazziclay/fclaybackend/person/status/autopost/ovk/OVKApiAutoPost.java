package com.fazziclay.fclaybackend.person.status.autopost.ovk;

import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.autopost.IAutoPost;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class OVKApiAutoPost implements IAutoPost {
    private final String host;
    private final String accessToken;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private long latestStatusUpdated;

    public OVKApiAutoPost(String host, String accessToken) {
        this.host = host;
        this.accessToken = accessToken;
    }

    public void setStatus(String status) {
        HttpUrl.Builder urlBuilder
                = Objects.requireNonNull(HttpUrl.parse("https://" + host + "/method/Account.saveProfileInfo")).newBuilder();
        urlBuilder.addQueryParameter("access_token", accessToken);
        urlBuilder.addQueryParameter("status", status);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Logger.debug("[ovk] " + response.body());
            }

            public void onFailure(Call call, IOException e) {
                Logger.debug("[ovk] " + e);
            }
        });
    }

    @Override
    public void postPersonStatus(@NotNull PersonStatus status) {
        if (System.currentTimeMillis() - latestStatusUpdated > 1000 * 60) {
            PlaybackDto playback = status.getHeadphones();
            if (playback != null) {
                setStatus(String.format("Listening in %s:%s: %s - %s", status.getDeviceName(), playback.getPlayer(), playback.getTitle(), playback.getArtist()));
                latestStatusUpdated = System.currentTimeMillis();
            }
        }
    }
}

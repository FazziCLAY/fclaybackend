package com.fazziclay.fclaybackend.person.status.autopost.ovk;

import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.autopost.AutoPostFilter;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class OVKApiAutoPost extends AutoPostFilter {
    private final String host;
    private final String accessToken;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public OVKApiAutoPost(String host, String accessToken) {
        super(Settings.builder()
                .rateLimit(60*1000)
                .build());
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
                response.close();
            }

            public void onFailure(Call call, IOException e) {
                Logger.debug("[ovk] " + e);
            }
        });
    }

    @Override
    public void sendMessageAbout(PersonStatus status) {
        PlaybackDto playback = status.getHeadphones();
        if (playback != null) {
            setStatus(String.format("[%s] Listening in %s:%s: %s - %s", new Date(), status.getDeviceName(), playback.getPlayer(), playback.getTitle(), playback.getArtist()));
        } else {
            setStatus(String.format("[%s] Currently music is paused.", new Date()));
        }
    }
}

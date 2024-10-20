package com.fazziclay.fclaybackend.person.status.autopost.telegram;

import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaybackend.config.TelegramBotConfig;
import com.fazziclay.fclaybackend.person.status.CuteTextPlayerGenerator;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.autopost.AutoPostFilter;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.json.JsonObject;
import java.util.Map;

public class TelegramBotAutoPost extends AutoPostFilter {
    private final TelegramBotApi api;
    private final String token;
    private final String chatId;
    private final Map<String, String> emojis;
    private TelegramBotConfig cfg;

    public TelegramBotAutoPost(String token, String chatId, Map<String, String> emojis) {
        this.token = token;
        this.chatId = chatId;
        this.emojis = emojis;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telegram.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(TelegramBotApi.class);
    }

    public TelegramBotAutoPost(TelegramBotConfig config) {
        this(config.getToken(), config.getChatId(), config.getEmojis());
        cfg = config;
    }

    public void sendMessageAbout(PersonStatus status) {
        PlaybackDto playback = status.getHeadphones();
        if (playback == null) {
            if (cfg.silenceMsg != null) {
                sendMessage(cfg.silenceMsg);
            }

        } else {
            String player = playback.getPlayer().toLowerCase();
            String emoji = emojis.getOrDefault(player, null);
            if (emoji == null) {
                emoji = emojis.getOrDefault("", "✨");
            }

            sendMessage(CuteTextPlayerGenerator.v1(status, emoji));


//            sendMessage(cfg.songMessage
//                    .replace("$(title)", playback.getTitle())
//                    .replace("$(artist)", playback.getArtist())
//                    .replace("$(album)", playback.getAlbum())
//                    .replace("$(player)", playback.getPlayer())
//                    .replace("$(playerEmoji)", emoji)
//            );
        }
    }

    public void sendMessage(String text) {
        api.sendMessage(token, new TelegramBotApi.SendMessageArgs(chatId, text, "HTML"))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                        Logger.debug(response);
                    }

                    @Override
                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable throwable) {
                        Logger.debug(throwable);
                    }
                });
    }

}

package com.fazziclay.fclaybackend.person.status.autopost.telegram;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import javax.json.JsonObject;

public interface TelegramBotApi {
    @POST("/bot{token}/sendMessage")
    Call<JsonObject> sendMessage(@Path("token") String token, @Body SendMessageArgs args);

    @AllArgsConstructor
    class SendMessageArgs {
        String chat_id;
        String text;
        String parse_mode;
    }
}

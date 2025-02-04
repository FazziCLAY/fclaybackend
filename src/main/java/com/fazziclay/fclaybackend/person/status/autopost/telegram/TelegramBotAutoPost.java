package com.fazziclay.fclaybackend.person.status.autopost.telegram;

import com.fazziclay.fclaybackend.person.status.CuteTextPlayerGenerator;
import com.fazziclay.fclaybackend.person.status.PersonStatus;
import com.fazziclay.fclaybackend.person.status.SongInfo;
import com.fazziclay.fclaybackend.person.status.autopost.IAutoPost;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TelegramBotAutoPost implements IAutoPost {
    private final TelegramBot api;
    private final String chatId;
    private SongInfo headPlayback;
    private Integer headMessageId;
    private boolean headIsNothing; // prevent edit on program first call after restart

    public TelegramBotAutoPost(String token, String chatId) {
        this.chatId = chatId;
        this.api = new TelegramBot(token);
    }

    @Override
    public void postPersonStatus(@NotNull PersonStatus status) {
        PlaybackDto playback = status.getHeadphones();
        SongInfo songInfo = SongInfo.create(playback);
        boolean edit = headIsNothing || Objects.equals(songInfo, headPlayback);
        String message = "UwU...";
        if (playback == null) {
            newHead("Nothing...");

        } else {
            message = CuteTextPlayerGenerator.v1(status, "✨");
            postHead(edit, message);
        }
        headPlayback = songInfo;
        headIsNothing = songInfo == null;
    }

    public void postHead(boolean edit, String message) {
        if (edit) {
            editHead(message);
        } else {
            newHead(message);
        }
    }

    public void newHead(String message) {
        SendResponse sendResponse = api.execute(new SendMessage(this.chatId, message));
        headMessageId = sendResponse.message().messageId();
    }

    public void editHead(String message) {
        if (headMessageId <= 0) {
            newHead(message);
            return;
        }
        BaseResponse sendResponse = api.execute(new EditMessageText(this.chatId, this.headMessageId, message));
        if (!sendResponse.isOk()) {
            newHead(message);
        }
    }
}

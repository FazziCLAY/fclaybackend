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
    private long latestTg = 0;
    private int headHashCode = 0;

    public TelegramBotAutoPost(String token, String chatId) {
        this.chatId = chatId;
        this.api = new TelegramBot(token);
    }

    @Override
    public void postPersonStatus(@NotNull PersonStatus status) {
        PlaybackDto playback = status.getHeadphones();
        SongInfo songInfo = SongInfo.create(playback);
        if (playback == null && headIsNothing) {
            return;
        }
        if (playback != null) {
            boolean edit = Objects.equals(songInfo, headPlayback);
            String message = CuteTextPlayerGenerator.v1(status, "✨");
            if (System.currentTimeMillis() - latestTg < 10000 || message.hashCode() == headHashCode) return;
            headHashCode = message.hashCode();
            postHead(edit, message);
        }
        headPlayback = songInfo;
        headIsNothing = songInfo == null;
    }

    public Integer postHead(boolean edit, String message) {
        latestTg = System.currentTimeMillis();
        if (edit) {
            return editHead(message);
        } else {
            return newHead(message);
        }
    }

    public Integer newHead(String message) {
        SendResponse sendResponse = api.execute(new SendMessage(this.chatId, message));
        return headMessageId = sendResponse.message().messageId();
    }

    public Integer editHead(String message) {
        if (headMessageId <= 0) {
            return newHead(message);
        }
        BaseResponse sendResponse = api.execute(new EditMessageText(this.chatId, this.headMessageId, message));
        if (!sendResponse.isOk()) {
            return newHead(message);
        }
        return this.headMessageId;
    }
}

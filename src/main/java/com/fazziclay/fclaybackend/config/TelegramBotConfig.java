package com.fazziclay.fclaybackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("application.properties")
@ConfigurationProperties(prefix = "fclaybackend.person-status.telegram-bot")
@Getter
@Setter
public class TelegramBotConfig {
    public String silenceMsg;
    public String songMessage = "<b>[%s%s]</b>\n<b>%s</b> - %s";
    private Map<String, String> emojis;
    private String token;
    private String chatId;

    private String ovkisserToken;
}

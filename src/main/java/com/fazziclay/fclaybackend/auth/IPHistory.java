package com.fazziclay.fclaybackend.auth;

import lombok.Getter;
import lombok.ToString;

@ToString
public class IPHistory {
    @Getter
    private final String ip;
    private long attempts = 0;
    private long latestAttemptTime = 0;

    public IPHistory(String ip) {
        this.ip = ip;
    }

    public IPHistory addAttempt(long requestTime) {
        attempts += 1;
        latestAttemptTime = requestTime;
        return this;
    }

    public boolean isBlocked(long requestTime) {
        return (attempts > 5) || requestTime - latestAttemptTime < 2000;
    }

    public static IPHistory build(String s) {
        return new IPHistory(s);
    }
}

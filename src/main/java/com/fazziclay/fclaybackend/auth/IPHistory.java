package com.fazziclay.fclaybackend.auth;

import lombok.Getter;

public class IPHistory {
    @Getter
    private final String ip;
    private long attempts = 0;
    private long latestAttemptTime = 0;

    public IPHistory(String ip) {
        this.ip = ip;
    }

    public IPHistory addAttempt() {
        attempts += 1;
        latestAttemptTime = System.currentTimeMillis();
        return this;
    }

    public boolean isBlocked() {
        return (attempts > 5) || System.currentTimeMillis() - latestAttemptTime < 2000;
    }

    public static IPHistory build(String s) {
        return new IPHistory(s).addAttempt();
    }
}

package com.fazziclay.fclaybackend.auth.exception;

import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.auth.IPHistory;
import org.springframework.http.HttpStatus;

public class IpRateLimitException extends HttpException {
    public IpRateLimitException(String ip, IPHistory history) {
        super("Rate limit for ip: " + ip + "; history: " + history, HttpStatus.TOO_MANY_REQUESTS);
    }
}

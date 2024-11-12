package com.fazziclay.fclaybackend.auth.exception;

import com.fazziclay.fclaybackend.HttpException;
import org.springframework.http.HttpStatus;

public class IpRateLimitException extends HttpException {
    public IpRateLimitException(String ip) {
        super("Rate limit for ip: " + ip, HttpStatus.TOO_MANY_REQUESTS);
    }
}

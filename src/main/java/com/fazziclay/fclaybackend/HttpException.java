package com.fazziclay.fclaybackend;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class HttpException extends ResponseStatusException {
    public HttpException(HttpStatusCode status) {
        super(status);
    }

    public HttpException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public HttpStatus getCode() {
        return HttpStatus.valueOf(getStatusCode().value());
    }
}

package com.fazziclay.fclaybackend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class HttpException extends RuntimeException {
    private HttpStatus code;

    public HttpException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }

    public HttpException(Exception exception, HttpStatus code) {
        super(exception);
        this.code = code;
    }

    public HttpException(String message, Exception exception, HttpStatus code) {
        super(message, exception);
        this.code = code;
    }
}

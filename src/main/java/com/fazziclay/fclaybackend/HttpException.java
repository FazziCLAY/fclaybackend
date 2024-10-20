package com.fazziclay.fclaybackend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@RequiredArgsConstructor
public class HttpException extends RuntimeException {
    @Getter private HttpStatus code;

    public HttpException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}

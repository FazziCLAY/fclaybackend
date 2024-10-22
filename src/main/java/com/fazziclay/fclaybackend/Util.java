package com.fazziclay.fclaybackend;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.function.Supplier;

public class Util {
    public static String time(Long millis) {
        if (millis == null) return null;
        long s = millis / 1000;
        return String.format("%d:%02d", s/60, s%60);
    }

    public static <T> ResponseEntity<?> handleError(Supplier<T> supplier, HttpStatus success) {
        try {
            return new ResponseEntity<>(supplier.get(), success);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (throwable instanceof HttpException httpException) {
                httpStatus = httpException.getCode();
            }
            JsonObject response = new JsonObject();

            JsonObject error = new JsonObject();
            error.addProperty("text", "An error occurred");
            error.addProperty("exception", throwable.toString());
            error.addProperty("httpErrorCode", httpStatus.value());
            error.addProperty("httpErrorPhrase", httpStatus.getReasonPhrase());
            error.addProperty("date", String.valueOf(new Date()));

            response.add("error", error);
            return new ResponseEntity<>(response, httpStatus);
        }
    }
}

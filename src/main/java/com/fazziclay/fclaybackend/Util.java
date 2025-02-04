package com.fazziclay.fclaybackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.function.Supplier;

public class Util {
    public static String time(Long millis) {
        if (millis == null) return null;
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
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
            ObjectMapper objectMapper = new ObjectMapper();

            // Создаём JSON-объект для ошибки
            ObjectNode error = objectMapper.createObjectNode();
            error.put("text", "An error occurred");
            error.put("exception", throwable.toString());
            error.put("httpErrorCode", httpStatus.value());
            error.put("httpErrorPhrase", httpStatus.getReasonPhrase());
            error.put("date", new Date().toString());

            // Заворачиваем в основной объект (если он нужен)
            ObjectNode response = objectMapper.createObjectNode();
            response.set("error", error);

            return new ResponseEntity<>(response, httpStatus);
        }
    }
}

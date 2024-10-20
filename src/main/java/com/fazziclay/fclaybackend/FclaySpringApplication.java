package com.fazziclay.fclaybackend;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@SpringBootApplication
@RestController
public class FclaySpringApplication {
	private static final PlaybackDto EMPTY_PATCH = new PlaybackDto(null, null, null, null, null, null, null, null);
	private final AtomicLong counter = new AtomicLong();

	public static void main(String[] args) {
		System.out.println("Started!");
		SpringApplication.run(FclaySpringApplication.class, args);
	}

	@GetMapping("/")
	public ResponseEntity<?> hello(@RequestHeader("X-Real-IP") String requestIp) {
		var map = new JsonObject();
		map.addProperty("hello", "Hello my dear user! Spring application is work correctly!");
		map.addProperty("java", "This message sent from Spring Boot (java)");
		map.addProperty("date", String.valueOf(new Date()));
		map.addProperty("yourIp", requestIp);
		map.addProperty("random", String.valueOf(new Random().nextInt()));
		map.addProperty("counter", String.valueOf(counter.incrementAndGet()));
		return new ResponseEntity<>(map, HttpStatusCode.valueOf(200));
	}

	public static PlaybackDto clone(PlaybackDto playbackDto) {
		return playbackDto.newWithPatch(EMPTY_PATCH);
	}

	public static <T> ResponseEntity<?> handle(Supplier<T> supplier, HttpStatus success) {
		try {
			return new ResponseEntity<>(supplier.get(), success);

		} catch (Throwable throwable) {
			HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			if (throwable instanceof HttpException httpException) {
				httpStatus = httpException.getCode();
			}
			JsonObject errorObject = new JsonObject();
			errorObject.addProperty("error", true);
			errorObject.addProperty("errorText", throwable.toString());
			errorObject.addProperty("timestamp", System.currentTimeMillis());
			throwable.printStackTrace();
			return new ResponseEntity<>(errorObject, httpStatus);
		}
	}
}

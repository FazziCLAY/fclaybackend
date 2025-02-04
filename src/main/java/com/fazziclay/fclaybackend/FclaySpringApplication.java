package com.fazziclay.fclaybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
public class FclaySpringApplication {
	private final AtomicLong counter = new AtomicLong();
	private final long startTime = System.currentTimeMillis();

	public static void main(String[] args) {
		System.out.println("Started!");
		SpringApplication.run(FclaySpringApplication.class, args);
	}

	@GetMapping("/")
	public ResponseEntity<?> hello(@RequestHeader("X-Real-IP") String requestIp) {
		var map = new HashMap<String, String>();
		map.put("hello", "Hello my dear user! Spring application is work correctly!");
		map.put("java", "This message sent from Spring Boot (java)");
		map.put("date", String.valueOf(new Date()));
		map.put("yourIp", requestIp);
		map.put("random", String.valueOf(new Random().nextInt()));
		map.put("counter", String.valueOf(counter.incrementAndGet()));
		map.put("uptime", String.valueOf(System.currentTimeMillis() - startTime));
		return new ResponseEntity<>(map, HttpStatusCode.valueOf(200));
	}

}

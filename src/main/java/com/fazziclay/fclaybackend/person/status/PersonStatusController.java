package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaybackend.FclaySpringApplication;
import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/person/status")
@AllArgsConstructor
public class PersonStatusController {

    private final PersonStatusService service;

    @GetMapping
    public ResponseEntity<?> read() {
        return handle(service::getActualPersonStatus, HttpStatus.OK);
    }


    @GetMapping("/cuteTextPlayer")
    public ResponseEntity<?> cuteTextPlayer() {
        return handle(() -> {
            try {
                return CuteTextPlayerGenerator.v1(service.getStatus(), "$").replace("\n", "<br>");
            } catch (Exception ignored) {
                return "";
            }
        }, HttpStatus.OK);
    }

    @GetMapping("/chartjs")
    public ResponseEntity<?> chartjs() {
        return handle(() -> service.getStatistic().asJsonObject(), HttpStatus.OK);
    }

    @PutMapping("/headphones")
    public ResponseEntity<?> put(@RequestHeader("Authorization") String authorization, @RequestBody PlaybackDto status) {
        Logger.debug("put: " + status);
        return handle(() -> service.putHeadphones(authorization, status), HttpStatus.OK);
    }

    @PatchMapping("/headphones")
    public ResponseEntity<?> patch(@RequestHeader("Authorization") String authorization, @RequestBody PlaybackDto status) {
        Logger.debug("patch: " + status);
        return handle(() -> service.patchHeadphones(authorization, status), HttpStatus.OK);
    }

    @DeleteMapping("/headphones")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorization) {
        Logger.debug("delete");
        return handle(() -> service.putHeadphones(authorization, null), HttpStatus.NO_CONTENT);
    }

    private <T> ResponseEntity<?> handle(Supplier<T> supplier, HttpStatus success) {
        return FclaySpringApplication.handle(supplier, success);
    }
}

package com.fazziclay.fclaybackend.person.status;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaybackend.person.status.service.PersonsStatusService;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping(path = {"/person/{person_name}/status", "/person/status"})
@AllArgsConstructor
public class PersonStatusController {
    private final PersonsStatusService service;

    // get root
    @GetMapping
    public ResponseEntity<?> read(@PathVariable(value = "person_name", required = false) String personName,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handle(() -> service.getActualPersonStatus(personName, authorization), HttpStatus.OK);
    }


    // get cute player
    @GetMapping("/cuteTextPlayer")
    public ResponseEntity<?> cuteTextPlayer(@PathVariable(value = "person_name", required = false) String personName,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handle(() -> {
            try {
                return CuteTextPlayerGenerator.v1(service.getActualPersonStatus(personName, authorization), "$").replace("\n", "<br>");
            } catch (Exception ignored) {
                return "";
            }
        }, HttpStatus.OK);
    }

    // get chartjs
    @GetMapping("/chartjs")
    public ResponseEntity<?> chartjs(@PathVariable(value = "person_name", required = false) String personName,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handle(() -> service.getStatistic(personName, authorization).asJsonObject(), HttpStatus.OK);
    }

    // PUT headphones
    @PutMapping("/headphones")
    public ResponseEntity<?> put(@PathVariable(value = "person_name", required = false) String personName,
                                 @RequestHeader(value = "Authorization", required = false) String authorization,
                                 @RequestBody PlaybackDto status) {
        return handle(() -> service.putHeadphones(personName, authorization, status), HttpStatus.OK);
    }

    // PATCH headphones
    @PatchMapping("/headphones")
    public ResponseEntity<?> patch(@PathVariable(value = "person_name", required = false) String personName,
                                   @RequestHeader(value = "Authorization", required = false) String authorization,
                                   @RequestBody PlaybackDto status) {
        return handle(() -> service.patchHeadphones(personName, authorization, status), HttpStatus.OK);
    }

    // DELETE headphones
    @DeleteMapping("/headphones")
    public ResponseEntity<?> delete(@PathVariable(value = "person_name", required = false) String personName,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handle(() -> service.putHeadphones(personName, authorization, null), HttpStatus.NO_CONTENT);
    }

    // handle errors
    private <T> ResponseEntity<?> handle(Supplier<T> supplier, HttpStatus success) {
        return Util.handleError(supplier, success);
    }
}

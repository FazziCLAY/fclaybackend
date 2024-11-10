package com.fazziclay.fclaybackend.auth;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaybackend.auth.dto.AddUserRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UsersController {
    private AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestHeader(value = "Authorization") String authToken, @RequestBody AddUserRequestDto requestDto) {
        return Util.handleError(() -> authService.addUser(authToken, requestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "Authorization") String authToken) {
        return Util.handleError(() -> authService.getAllUsers(authToken), HttpStatus.OK);
    }
}

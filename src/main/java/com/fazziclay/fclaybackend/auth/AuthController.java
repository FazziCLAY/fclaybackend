package com.fazziclay.fclaybackend.auth;

import com.fazziclay.fclaybackend.Util;
import com.fazziclay.fclaybackend.auth.dto.LoginRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String authToken, @RequestBody(required = false) LoginRequestDto requestDto) {
        return Util.handleError(() -> authService.login(authToken, requestDto), HttpStatus.OK);
    }
}

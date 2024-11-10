package com.fazziclay.fclaybackend.auth;

import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.auth.db.AuthDB;
import com.fazziclay.fclaybackend.auth.db.Session;
import com.fazziclay.fclaybackend.auth.db.User;
import com.fazziclay.fclaybackend.auth.dto.AddUserRequestDto;
import com.fazziclay.fclaybackend.auth.dto.LoginRequestDto;
import com.fazziclay.fclaybackend.auth.dto.LoginResponseDto;
import com.fazziclay.fclaybackend.auth.dto.UserDto;
import com.fazziclay.fclaybackend.auth.misc.Permissions;
import com.fazziclay.fclaybackend.auth.misc.Validate;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthService {
    private AuthDB db;

    @PostConstruct
    private void init() {
        db = new AuthDB();
        db.init();
    }

    @SneakyThrows
    public LoginResponseDto login(String authToken, LoginRequestDto requestDto) {
        // is provided token in header
        if (authToken != null) {
            val session = db.getSession(authToken);
            if (Validate.isSessionActive(session)) {
                db.regenerateSessionToken(session);
                return LoginResponseDto.builder()
                        .accessToken(session.getAccessToken())
                        .build();
            }
            throw new HttpException("Invalid auth token", HttpStatus.UNAUTHORIZED);
        }

        // is provided request body (expected with login and password)
        if (requestDto != null) {
            Session session = db.tryToCreateSession(requestDto);
            return LoginResponseDto.builder()
                    .accessToken(session.getAccessToken())
                    .build();
        }

        throw new HttpException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    // add user
    public UserDto addUser(String authToken, AddUserRequestDto requestDto) {
        authOrThrow(authToken, Permissions.ADMIN_MANAGE_USERS);

        val user = db.createUser(requestDto.getUsername(), requestDto.getPassword());
        return user.toDto();
    }

    public User[] getAllUsers(String authToken) {
        authOrThrow(authToken, Permissions.ADMIN_MANAGE_USERS);

        return getDb().getAllUsers();
    }

    // auth
    private void authOrThrow(String authToken, Permissions... required) {
        val session = db.getSession(authToken);
        if (Validate.isSessionActive(session)) {
            session.setAccessTime(System.currentTimeMillis());
            val user = db.getUserById(session.getUserId());
            if (user == null) {
                throw new HttpException("Session for non-exist user...", HttpStatus.UNAUTHORIZED);
            }
            if (!Validate.isUserPerm(user, required)) {
                throw new HttpException("No perms for this", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new HttpException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
}

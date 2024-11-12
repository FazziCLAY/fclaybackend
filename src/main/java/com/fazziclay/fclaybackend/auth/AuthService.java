package com.fazziclay.fclaybackend.auth;

import com.fazziclay.fclaybackend.FclayConfig;
import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.auth.db.AuthDB;
import com.fazziclay.fclaybackend.auth.db.Session;
import com.fazziclay.fclaybackend.auth.db.TabItem;
import com.fazziclay.fclaybackend.auth.db.User;
import com.fazziclay.fclaybackend.auth.dto.*;
import com.fazziclay.fclaybackend.auth.exception.IpRateLimitException;
import com.fazziclay.fclaybackend.auth.misc.Permissions;
import com.fazziclay.fclaybackend.auth.misc.Validate;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.json.JsonArray;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Setter
@Getter
public class AuthService {
    private AuthDB db;
    private LoadingCache<String, IPHistory> ipHistoryCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(IPHistory::build);

    public AuthService(@Autowired FclayConfig config) {
        db = new AuthDB(new File(config.getAuthDbDir()));
        db.init();
    }

    @SneakyThrows
    public LoginResponseDto login(@Nullable String authToken, @Nullable LoginRequestDto requestDto, @Nullable String requestIp) {
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
            Objects.requireNonNull(requestIp);
            IPHistory ipHistory = ipHistoryCache.get(requestIp);
            long requestTime = System.currentTimeMillis();
            if (ipHistory.isBlocked(requestTime)) {
                throw new IpRateLimitException(requestIp, ipHistory);
            }
            ipHistory.addAttempt(requestTime);
            Session session = db.tryToCreateSession(requestDto);
            return LoginResponseDto.builder()
                    .accessToken(session.getAccessToken())
                    .build();
        }

        throw new HttpException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    public List<TabItem> getNoteTabs(String authToken) {
        var auth = authOrThrow(authToken, Permissions.NOTES_TABS_READ);
        return db.getUserNoteTabs(auth.getRight());
    }

    public List<TabItem> setNoteTabs(String authToken, List<TabItem> post) {
        var auth = authOrThrow(authToken, Permissions.NOTES_TABS_WRITE);
        return db.setUserNoteTabs(auth.getRight(), post);
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
    public Pair<Session, User> authOrThrow(String authToken, Permissions... required) {
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
            return Pair.of(session, user);
        } else {
            throw new HttpException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

    public void changePassword(String authToken, ChangePasswordRequestDto requestDto) {
        var auth = authOrThrow(authToken, Permissions.CHANGE_PASSWORD);
        Objects.requireNonNull(requestDto);
        Objects.requireNonNull(requestDto.getNew_password());
        Objects.requireNonNull(requestDto.getOld_password());
        db.changePassword(auth.getRight(), requestDto);
    }

    public UserDto getMe(String authToken) {
        val sessionUserPair = authOrThrow(authToken);
        return sessionUserPair.getRight().toDto();
    }

    public List<TabItem> setNoteTabs(String authorization, JsonArray tabsToPost) {
        return null;
    }
}

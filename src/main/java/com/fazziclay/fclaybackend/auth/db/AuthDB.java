package com.fazziclay.fclaybackend.auth.db;

import com.fazziclay.fclaybackend.HttpException;
import com.fazziclay.fclaybackend.Logger;
import com.fazziclay.fclaybackend.ThrowableConsumer;
import com.fazziclay.fclaybackend.auth.dto.ChangePasswordRequestDto;
import com.fazziclay.fclaybackend.auth.dto.LoginRequestDto;
import com.fazziclay.fclaybackend.auth.misc.Generator;
import com.fazziclay.fclaybackend.auth.misc.Permissions;
import com.fazziclay.fclaybackend.auth.misc.Validate;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;


// почему я избегаю баз данных?
public class AuthDB {
    private final File dir;
    private File file;
    private File fileBak;
    private DbStorage dbStorage;
    private Gson gson = new Gson();
    private SaveThread save = new SaveThread();

    public AuthDB(File dir) {
        this.dir = dir;
    }

    @SneakyThrows
    public void init() {
        file = new File(dir, "authdb.json");
        fileBak = new File(dir,"authdb.json.bak");

        if (file.exists()) {
            bakAct(file -> {
                dbStorage = gson.fromJson(Files.readString(file.toPath()), DbStorage.class);
            }, () -> {
                throw new RuntimeException("Failed to init AuthDB: no one file success to load");
            });
        } else {
            dbStorage = new DbStorage();
            createUser("admin", "admin", Permissions.GOD);
            save();
        }
        save.start();
        Logger.debug("AuthDB initialized!");
    }

    private void bakAct(ThrowableConsumer<Exception, File> act, Runnable noOne) {
        File[] attempt = {file, fileBak};
        for (File f : attempt) {
            try {
                act.accept(f);
                return;
            } catch (Exception e) {
                Logger.debug("Error bakAct file=" + f);
                e.printStackTrace();
            }
        }
        noOne.run();
    }



    // ========= USERS =========
    public User[] getAllUsers() {
        return dbStorage.usersById.values().toArray(new User[0]);
    }

    @Nullable
    public User getUserByName(String username) {
        return getUserById(dbStorage.userNameToId.get(username));
    }

    @Nullable
    public User getUserById(UUID id) {
        return getUserById(id.toString());
    }

    @Nullable
    public User getUserById(String id) {
        return dbStorage.getUsersById().get(id);
    }

    public User createUser(String username, String password, Permissions... perms) {
        if (getUserByName(username) != null) {
            throw new RuntimeException("User with this username already exist!");
        }
        if (!Validate.isValidUsername(username) || Validate.isValidPassword(password)) {
            throw new HttpException("username or password incorrect", HttpStatus.BAD_REQUEST);
        }


        User user = User.builder()
                .username(username)
                .passwordSha256(Validate.sha256sum(password))
                .createdAt(System.currentTimeMillis())
                .permissions(perms)
                .id(UUID.randomUUID())
                .build();

        dbStorage.usersById.put(user.getId().toString(), user);
        dbStorage.userNameToId.put(user.getUsername(), user.getId().toString());
        return user;
    }

    // direct patch password;
    private void changePasswordDirect(User user, String newPass) {
        dataManipulate(() -> {
            Validate.validatePassword(newPass);
            user.setPasswordSha256(Validate.sha256sum(newPass));
        });
    }

    public void changePassword(User user, ChangePasswordRequestDto dto) {
        dataManipulate(() -> {
            if (!Validate.isUserPasswordEquals(user, dto.getOld_password())) {
                throw new HttpException("Old password incorrect", HttpStatus.FORBIDDEN);
            }
            changePasswordDirect(user, dto.getNew_password());
        });
    }

    public List<TabItem> getUserNoteTabs(User user) {
        return user.getTabs();
    }

    public List<TabItem> setUserNoteTabs(User user, List<TabItem> tabItems) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(tabItems);
        for (TabItem tabItem : tabItems) {
            Objects.requireNonNull(tabItem);
            Objects.requireNonNull(tabItem.getName());
            Objects.requireNonNull(tabItem.getAccessToken());
        }
        dataManipulate(() -> {
            user.setTabs(tabItems);
        });
        return getUserNoteTabs(user);
    }

    // ========= SESSIONS =========
    @Nullable
    public Session getSession(String authToken) {
        val session = dbStorage.sessions.get(authToken);
        if (session != null) session.setAccessTime(System.currentTimeMillis());
        return session;
    }

    public Session regenerateSessionToken(Session session) {
        var nt = session.getUserId() + Generator.genAccessToken();
        dataManipulate(() -> {
            dbStorage.getSessions().remove(session.getAccessToken());

            session.setAccessToken(nt);
            dbStorage.getSessions().put(nt, session);
        });
        return session;
    }

    @SneakyThrows
    public Session tryToCreateSession(LoginRequestDto requestDto) {
        Objects.requireNonNull(requestDto);
        Objects.requireNonNull(requestDto.getUsername());
        Objects.requireNonNull(requestDto.getPassword());

        var username = requestDto.getUsername();
        var password = requestDto.getPassword();
        var unauthorized = new HttpException("Failed to login", HttpStatus.UNAUTHORIZED);

        val user = getUserByName(username);
        if (user == null) {
            throw unauthorized;
        }

        if (!Validate.isUserPasswordEquals(user, password)) {
            throw unauthorized;
        }

        // here all params verified, password passed success
        return genSessionAndCreate(user);
    }

    // direct create and add session for user
    private Session genSessionAndCreate(User user) {
        var ref = new Object() {
            Session session;
        };
        dataManipulate(() -> {
            ref.session = new Session.SessionBuilder()
                    .id(UUID.randomUUID())
                    .accessToken(user.getId() + Generator.genAccessToken())
                    .createTime(System.currentTimeMillis())
                    .accessTime(System.currentTimeMillis())
                    .userId(user.getId())
                    .build();
            dbStorage.getSessions().put(ref.session.getAccessToken(), ref.session);
        });

        return ref.session;
    }



    // ========= DB OBJECT =========
    @Setter
    @Getter
    private static class DbStorage {
        private Map<String, User> usersById = new HashMap<>();
        private Map<String, String> userNameToId = new HashMap<>();
        private Map<String, Session> sessions = new HashMap<>();
    }



    // ========= SAVINGS =========
    @SneakyThrows
    private void waitForSaving() {
        while (save.saving) {
            Thread.sleep(10);
        }
    }

    private void dataManipulate(Runnable r) {
        waitForSaving();
        r.run();
        save();
    }

    private void save() {
        save.needSave();
    }

    private class SaveThread extends Thread {
        private long saves;
        private boolean saving;
        private long needsSave;
        private long firstNeedSaveTime;
        private long lastNeedSaveTime;

        public SaveThread() {
            setName("AuthDB-Thread");
            setDaemon(true);
        }

        private boolean isSaving() {
            return saving;
        }

        public void needSave() {
            lastNeedSaveTime = System.currentTimeMillis();
            needsSave++;
            if (firstNeedSaveTime <= 0) {
                firstNeedSaveTime = lastNeedSaveTime;
            }
        }

        private void markSaved() {
            saves++;
            needsSave = 0;
            firstNeedSaveTime = 0;
            lastNeedSaveTime = 0;
            saving = false;
        }

        private void save() {
            saving = true;
            try {
                saveInternal();
            } finally {
                markSaved();
            }
        }

        @SneakyThrows
        private void saveInternal() {
            boolean first = !file.exists();
            if (!first) {
                Files.copy(file.toPath(), fileBak.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.createDirectories(file.getParentFile().toPath());
            Files.writeString(file.toPath(), gson.toJson(dbStorage), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            if (first) {
                Files.copy(file.toPath(), fileBak.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Logger.debug("Saved success");
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (needsSave > 0) {
                        long _curr = System.currentTimeMillis();
                        long sinceFirst = _curr - firstNeedSaveTime;
                        long sinceLast = _curr - lastNeedSaveTime;
                        if (sinceFirst >= (2000 - sinceLast)) {
                            try {
                                save();
                            } catch (Throwable e) {
                                Logger.debug("ERROR WHILE SAVING");
                                e.printStackTrace();
                            }
                            Logger.debug("AuthDB save");
                        }
                    }
                    long delay = 200 - (needsSave * 20);
                    Thread.sleep(Math.max(0, Math.min(delay, 1444)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}

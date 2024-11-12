package com.fazziclay.fclaybackend.auth.misc;

import com.fazziclay.fclaybackend.auth.db.Session;
import com.fazziclay.fclaybackend.auth.db.User;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Validate {
    public static boolean isSessionActive(Session session) {
        return session != null && !session.isExpired();
    }

    public static boolean isUserPerm(User user, Permissions... permissions) {
        Objects.requireNonNull(user);
        if (user.isBanned()) return false;
        if (contains(user.getPermissions(), Permissions.GOD)) return true;
        for (Permissions permission : permissions) {
            if (!contains(user.getPermissions(), permission)) {
                return permission.getDef().apply(user);
            }
        }
        return true;
    }

    private static <T> boolean contains(T[] list, T t) {
        for (T t1 : list) {
            if (t1 == t) return true;
        }
        return false;
    }

    public static boolean isValidPassword(String pass) {
        if (pass == null || pass.trim().isEmpty() || pass.length() > 100) {
            return false;
        }

        return true;
    }

    public static void validatePassword(String password) {
        if (!isValidPassword(password))
            throw new RuntimeException("Password incorrect format");
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty() || username.length() > 50) {
            return false;
        }
        return !username.contains(" ");
    }

    public static void validateUsername(String username) {
        if (!isValidUsername(username))
            throw new RuntimeException("Username incorrect format");
    }


    public static boolean isUserPasswordEquals(User user, String password) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        val userPass = user.getPasswordSha256();
        val request = sha256sum(password);
        return Objects.equals(userPass, request);
    }

    public static String sha256sum(String str) {
        Objects.requireNonNull(str);
        return DigestUtils.sha256Hex(str.getBytes(StandardCharsets.UTF_8));
    }
}

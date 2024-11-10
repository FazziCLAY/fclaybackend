package com.fazziclay.fclaybackend.auth.misc;

import com.fazziclay.fclaybackend.auth.db.Session;
import com.fazziclay.fclaybackend.auth.db.User;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
    private static final String passwordRegexp = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,100}$";

    private static final Pattern passwordRegexpPattern = Pattern.compile(passwordRegexp);

    public static boolean isSessionActive(Session session) {
        return session != null && !session.isExpired();
    }

    public static boolean isUserPerm(User user, Permissions... permissions) {
        Objects.requireNonNull(user);
        if (user.isBanned()) return false;
        if (contains(user.getPermissions(), Permissions.GOD)) return true;
        for (Permissions permission : permissions) {
            if (!contains(user.getPermissions(), permission)) {
                return false;
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

    public static boolean validatePassword(String pass) {
        if (pass == null || pass.trim().isEmpty() || pass.length() > 100) {
            return false;
        }

        Matcher m = passwordRegexpPattern.matcher(pass);
        return m.matches();
    }

    public static boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty() || username.length() > 50) {
            return false;
        }
        return !username.contains(" ");
    }

    public static boolean checkUserPassword(User user, String password) {
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

package com.fazziclay.fclaybackend.auth.misc;

import com.fazziclay.fclaybackend.auth.db.User;
import lombok.Getter;

import java.util.function.Function;


public enum Permissions {
    NOTES_TABS_READ(Companion.DEFAULT_YES),
    NOTES_TABS_WRITE(Companion.DEFAULT_YES),
    CHANGE_PASSWORD(Companion.DEFAULT_NOT_GUEST),
    ADMIN_RELOAD_CONFIGS,
    ADMIN_MANAGE_USERS,
    GOD,

    ;



    @Getter
    private final Function<User, Boolean> def;

    Permissions() {
        def = Companion.DEFAULT_NO;
    }

    Permissions(Function<User, Boolean> _default) {
        def = _default;
    }

    private static class Companion {
        protected static final Function<User, Boolean> DEFAULT_NO = (user) -> false;
        protected static final Function<User, Boolean> DEFAULT_YES = (user) -> true;
        protected static final Function<User, Boolean> DEFAULT_NOT_GUEST = (user) -> !user.getUsername().equalsIgnoreCase("guest");
    }
}

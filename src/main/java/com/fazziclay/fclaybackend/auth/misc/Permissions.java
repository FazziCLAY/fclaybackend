package com.fazziclay.fclaybackend.auth.misc;

import lombok.Getter;

@Getter
public enum Permissions {
    NOTES_TABS_READ(true),
    NOTES_TABS_WRITE(true),
    CHANGE_PASSWORD(true),
    ADMIN_RELOAD_CONFIGS,
    ADMIN_MANAGE_USERS,
    GOD,

    ;

    private final boolean def;
    private Permissions() {
        def = false;
    }

    private Permissions(boolean _default) {
        def = _default;
    }
}

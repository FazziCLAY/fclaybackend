package com.fazziclay.fclaybackend.person.status;

import java.util.HashMap;
import java.util.Map;

public enum PersonState {
    UNKNOWN(-1),

    /**
     * Получаю информацию пассивно. Слушаю музыку и ничего не делаю
     * т.к. музыка затмевает ум.
     */
    PASSIVE_INFORMATION_RECEIVING(0),

    /**
     * Активно работаю (над каким-либо проектом)
     */
    ACTIVE_WORKING(1),

    /**
     * Активно что-либо изучаю
     */
    ACTIVE_LEARNING(2),
    ;

    private static final Map<Integer, PersonState> CACHED_VALUES = new HashMap<>();
    private final int value;

    static {
        for (PersonState state : values()) {
            CACHED_VALUES.put(state.value, state);
        }
    }

    PersonState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PersonState fromValue(int value) {
        return CACHED_VALUES.get(value);
    }
}

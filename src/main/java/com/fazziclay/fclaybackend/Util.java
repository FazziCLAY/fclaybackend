package com.fazziclay.fclaybackend;

public class Util {
    public static String time(Long millis) {
        if (millis == null) return null;
        long s = millis / 1000;
        return String.format("%d:%02d", s/60, s%60);
    }
}

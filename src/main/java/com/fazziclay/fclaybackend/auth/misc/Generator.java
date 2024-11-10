package com.fazziclay.fclaybackend.auth.misc;

import java.security.SecureRandom;
import java.util.UUID;

public class Generator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String genAccessToken() {
        return System.currentTimeMillis() + secureRandom.hashCode() + secureRandom.nextInt() + UUID.randomUUID().toString().replace("-", "") + 0 + new UUID(secureRandom.nextLong(), secureRandom.nextLong()).toString().replace("-", "");
    }
}

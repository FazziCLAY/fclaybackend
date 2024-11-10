package com.fazziclay.fclaybackend.auth.db;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session {
    private UUID id;
    private UUID userId;
    private String accessToken;
    private long createTime;
    private long accessTime;
    private boolean expired;
}

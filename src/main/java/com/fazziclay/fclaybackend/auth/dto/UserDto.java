package com.fazziclay.fclaybackend.auth.dto;

import com.fazziclay.fclaybackend.auth.misc.Permissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private Permissions[] permissions = new Permissions[0];
    private long createdAt;
}

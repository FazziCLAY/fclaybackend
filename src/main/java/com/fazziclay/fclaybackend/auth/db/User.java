package com.fazziclay.fclaybackend.auth.db;

import com.fazziclay.fclaybackend.auth.dto.UserDto;
import com.fazziclay.fclaybackend.auth.misc.Permissions;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private UUID id;
    private String username;
    private String passwordSha256;
    private long createdAt;
    private boolean banned;
    @Builder.Default
    private Permissions[] permissions = new Permissions[0];
    @Builder.Default
    private List<TabItem> tabs = new ArrayList<>();

    public UserDto toDto() {
        return new UserDto(id, username, permissions, createdAt);
    }
}

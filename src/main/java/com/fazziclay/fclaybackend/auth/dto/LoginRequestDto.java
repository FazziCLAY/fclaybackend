package com.fazziclay.fclaybackend.auth.dto;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class LoginRequestDto {
    @Nullable private String username;
    @Nullable private String password;
}

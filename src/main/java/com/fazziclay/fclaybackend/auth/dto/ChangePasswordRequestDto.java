package com.fazziclay.fclaybackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ChangePasswordRequestDto {
    private String old_password;
    private String new_password;
}

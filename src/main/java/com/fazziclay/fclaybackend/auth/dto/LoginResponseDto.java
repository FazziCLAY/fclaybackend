package com.fazziclay.fclaybackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String accessToken;
}

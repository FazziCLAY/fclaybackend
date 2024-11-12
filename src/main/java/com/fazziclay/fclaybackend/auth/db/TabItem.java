package com.fazziclay.fclaybackend.auth.db;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TabItem {
    private String name;
    private String accessToken;
}

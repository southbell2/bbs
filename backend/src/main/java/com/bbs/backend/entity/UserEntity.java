package com.bbs.backend.entity;

import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String id;
    private String email;
    private String password;
    private String nickname;
    private LocalDate joinDate;
}

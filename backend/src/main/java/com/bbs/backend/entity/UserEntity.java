package com.bbs.backend.entity;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String id;
    private String email;
    private String password;
    private String username;
    private LocalDate joinDate;
}

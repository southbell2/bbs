package com.bbs.backend.entity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {
    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int id;
    private int views;
    private String userId;
}

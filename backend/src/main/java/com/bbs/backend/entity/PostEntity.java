package com.bbs.backend.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
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
    private List<String> imageFileNames;
}

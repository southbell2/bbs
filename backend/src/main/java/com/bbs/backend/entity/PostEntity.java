package com.bbs.backend.entity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime dateTime;
    private int postNumber;
    private int postViews;
    private String email;
}

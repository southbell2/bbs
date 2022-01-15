package com.bbs.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime dateTime;
    private int postNumber;
    private int postViews;
}

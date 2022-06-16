package com.bbs.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    private int id;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private int postId;
    private String userId;
}

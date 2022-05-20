package com.bbs.backend.dto.post;

import com.bbs.backend.entity.PostEntity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDTO {

    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int id;
    private int views;
    private boolean writer;

    public GetPostDTO(final PostEntity postEntity, boolean writer) {
        this.username = postEntity.getUsername();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.createdAt = postEntity.getCreatedAt();
        this.id = postEntity.getId();
        this.views = postEntity.getViews();
        this.writer = writer;
    }

}

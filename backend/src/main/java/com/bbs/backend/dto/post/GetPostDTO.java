package com.bbs.backend.dto.post;

import com.bbs.backend.entity.PostEntity;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "게시글 상세 정보를 위한 객체")
public class GetPostDTO {

    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int id;
    private int views;

    public GetPostDTO(final PostEntity postEntity) {
        this.username = postEntity.getUsername();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.createdAt = postEntity.getCreatedAt();
        this.id = postEntity.getId();
        this.views = postEntity.getViews();
    }

}

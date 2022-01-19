package com.bbs.backend.dto;

import com.bbs.backend.entity.PostEntity;
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

    public PostDTO(final PostEntity postEntity) {
        this.nickname = postEntity.getNickname();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.dateTime = postEntity.getDateTime();
        this.postNumber = postEntity.getPostNumber();
        this.postViews = postEntity.getPostViews();
    }

    public static PostEntity toEntity(final PostDTO postDTO) {
        PostEntity postEntity = PostEntity.builder()
                .postViews(postDTO.getPostViews())
                .postNumber(postDTO.getPostNumber())
                .content(postDTO.getContent())
                .dateTime(postDTO.getDateTime())
                .nickname(postDTO.getNickname())
                .title(postDTO.getTitle())
                .build();

        return postEntity;
    }
}

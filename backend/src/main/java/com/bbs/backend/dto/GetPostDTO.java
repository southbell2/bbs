package com.bbs.backend.dto;

import com.bbs.backend.entity.PostEntity;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "게시글 상세 정보를 위한 객체")
public class GetPostDTO {

    private String nickname;
    private String title;
    private String content;
    private LocalDateTime dateTime;
    private int postNumber;
    private int postViews;

    public GetPostDTO(final PostEntity postEntity) {
        this.nickname = postEntity.getNickname();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.dateTime = postEntity.getDateTime();
        this.postNumber = postEntity.getPostNumber();
        this.postViews = postEntity.getPostViews();
    }

}

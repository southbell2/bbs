package com.bbs.backend.dto;

import com.bbs.backend.entity.PostEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "게시글 상세 정보를 위한 객체")
public class PostDTO {
    @ApiModelProperty("글쓴이")
    private String nickname;
    @ApiModelProperty("글 제목")
    private String title;
    @ApiModelProperty("글 내용")
    private String content;
    @ApiModelProperty("작성 날짜와 시간 입력 X")
    private LocalDateTime dateTime;
    @ApiModelProperty("글 번호 입력 X")
    private int postNumber;
    @ApiModelProperty("조회수 입력 X")
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

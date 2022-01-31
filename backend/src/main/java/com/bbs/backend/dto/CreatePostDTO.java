package com.bbs.backend.dto;

import com.bbs.backend.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreatePostDTO {
    @NotBlank
    @Size(max=15)
    private String nickname;

    @NotBlank
    @Size(max=50)
    private String title;

    @NotBlank
    @Size(max=5000)
    private String content;

    public static PostEntity toEntity(CreatePostDTO createPostDTO) {
        return PostEntity.builder()
                .nickname(createPostDTO.getNickname())
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .build();
    }
}

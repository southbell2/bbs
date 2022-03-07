package com.bbs.backend.dto.post;

import com.bbs.backend.entity.PostEntity;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatePostDTO {
    @NotBlank(message = "제목을 입력하세요")
    @Size(max=50, message = "제목은 50자를 넘으면 안 됩니다")
    private String title;

    @NotBlank(message = "내용을 입력하세요")
    @Size(max=5000, message = "내용은 5000자를 넘으면 안 됩니다")
    private String content;

    public static PostEntity toEntity(CreatePostDTO createPostDTO) {
        return PostEntity.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .build();
    }
}

package com.bbs.backend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCommentDTO {
    @NotBlank(message = "내용을 입력하세요")
    @Size(max=100, message = "내용은 100자를 넘으면 안 됩니다")
    private String content;

    private int postId;
}

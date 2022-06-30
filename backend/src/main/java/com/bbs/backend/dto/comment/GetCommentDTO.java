package com.bbs.backend.dto.comment;

import com.bbs.backend.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetCommentDTO {
    private List<CommentEntity> commentList;
    private int allCommentNumber;
}

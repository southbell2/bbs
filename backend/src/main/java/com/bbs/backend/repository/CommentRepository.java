package com.bbs.backend.repository;

import com.bbs.backend.entity.CommentEntity;

import java.util.List;

public interface CommentRepository {
    void createComment(CommentEntity commentEntity);
    List<CommentEntity> findCommentByPostId(int postId, int commentPageNumber);
}

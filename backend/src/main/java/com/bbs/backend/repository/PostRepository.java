package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;

import java.util.List;

public interface PostRepository {
    List<PostEntity> findAll();
    PostEntity createPost(PostEntity postEntity);
    PostEntity findPostByNumber(int number);
    void updatePost(PostEntity postEntity, int postNumber);

    void deletePost(int number);
}

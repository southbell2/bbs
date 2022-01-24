package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;

import java.util.List;

public interface PostRepository {
    List<PostEntity> findAll();
    PostEntity createPost(PostEntity postEntity);
    PostEntity findPostByNumber(int number);
    void updatePost(PostEntity postEntity);

    void deletePost(int number);
}

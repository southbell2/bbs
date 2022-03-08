package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<PostEntity> findPageByNumber(int number);
    PostEntity createPost(PostEntity postEntity);
    Optional<PostEntity> findPostById(int id);
    void updatePost(PostEntity postEntity, int id);
    int getAllPostNumber();
    void deletePost(int id);
}

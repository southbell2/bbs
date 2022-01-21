package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;

import java.util.List;

public interface PostRepository {
    List<PostEntity> getPostList();
    void savePost(PostEntity postEntity);
    PostEntity getPostByNumber(int number);
    void updatePost(PostEntity postEntity);

    void deletePost(int number);
}

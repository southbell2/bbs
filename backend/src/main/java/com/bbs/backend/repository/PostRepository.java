package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;

import java.util.List;

public interface PostRepository {
    List<PostEntity> getPostList();
    PostEntity savePost(PostEntity postEntity);
}

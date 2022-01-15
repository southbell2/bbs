package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostMemoryRepository implements PostRepository {
    private final List<PostEntity> postEntityList = new ArrayList<>();

    @Override
    public List<PostEntity> getPostList() {
        return postEntityList;
    }

    @Override
    public PostEntity savePost(PostEntity postEntity) {
        return null;
    }
}

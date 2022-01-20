package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostMemoryRepository implements PostRepository {
    private final List<PostEntity> postEntityList = new ArrayList<>();
    private int postNumber;

    @Override
    public List<PostEntity> getPostList() {
        return postEntityList;
    }

    @Override
    public PostEntity savePost(PostEntity postEntity) {
        postNumber++;
        postEntity.setPostNumber(postNumber);
        postEntityList.add(postEntity);
        return postEntity;
    }

    @Override
    public PostEntity getPostByNumber(int postNumber) {
        for (PostEntity postEntity : postEntityList) {
            if (postEntity.getPostNumber() == postNumber) {
                return postEntity;
            }
        }

        return null;
    }
}

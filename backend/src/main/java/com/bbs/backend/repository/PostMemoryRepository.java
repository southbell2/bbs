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
    public List<PostEntity> findAll() {
        return postEntityList;
    }

    @Override
    public PostEntity createPost(PostEntity postEntity) {
        postNumber++;
        postEntity.setPostNumber(postNumber);
        postEntityList.add(postEntity);

        return postEntity;
    }

    @Override
    public PostEntity findPostByNumber(int postNumber) {
        for (PostEntity postEntity : postEntityList) {
            if (postEntity.getPostNumber() == postNumber) {
                postEntity.setPostViews(postEntity.getPostViews() + 1);
                return postEntity;
            }
        }

        return null;
    }

    @Override
    public void updatePost(PostEntity postEntity) {
        for (PostEntity post : postEntityList) {
            if (post.getPostNumber() == postEntity.getPostNumber()) {
                post.setTitle(postEntity.getTitle());
                post.setContent(postEntity.getContent());
                return;
            }
        }
    }

    @Override
    public void deletePost(int number) {
        for (int i = 0; i < postEntityList.size(); i++) {
            if (postEntityList.get(i).getPostNumber() == number) {
                postEntityList.remove(i);
                return;
            }
        }
    }
}

package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Repository
public class PostMemoryRepository implements PostRepository {
    private final List<PostEntity> postEntityList = new ArrayList<>();
    private int postNumber;

    @Override
    public List<PostEntity> findPageByNumber(int number) {
        return postEntityList;
    }

    @Override
    public PostEntity createPost(PostEntity postEntity) {
        postEntity.setId(++postNumber);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntityList.add(postEntity);

        return postEntity;
    }

    @Override
    public Optional<PostEntity> findPostById(int postNumber) {
//        for (PostEntity postEntity : postEntityList) {
//            if (postEntity.getId() == postNumber) {
//                postEntity.setViews(postEntity.getViews() + 1);
//                return postEntity;
//            }
//        }

        return null;
    }

    @Override
    public void updatePost(PostEntity postEntity, int id) {
//        for (PostEntity post : postEntityList) {
//            if (post.getId() == postNumber) {
//                post.setTitle(postEntity.getTitle());
//                post.setContent(postEntity.getContent());
//                return;
//            }
//        }
    }

    @Override
    public int getAllPostNumber() {
        return 0;
    }

    @Override
    public void deletePost(int number) {
        for (int i = 0; i < postEntityList.size(); i++) {
            if (postEntityList.get(i).getId() == number) {
                postEntityList.remove(i);
                return;
            }
        }
    }
}

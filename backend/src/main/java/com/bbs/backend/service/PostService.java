package com.bbs.backend.service;

import com.bbs.backend.dto.PostDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    @Autowired
    private final PostRepository postRepository;

    public List<PostEntity> getPostList() {
        return postRepository.getPostList();
    }

    public void savePost(PostEntity postEntity) {
        postRepository.savePost(postEntity);
    }

    public PostEntity getPostByNumber(int number) {
        return postRepository.getPostByNumber(number);
    }

    public void updatePost(PostEntity postEntity) {
        postRepository.updatePost(postEntity);
    }

    public void deletePost(int number) {
        postRepository.deletePost(number);
    }

}

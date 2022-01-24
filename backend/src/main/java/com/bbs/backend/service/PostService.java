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

    public List<PostEntity> findAll() {
        return postRepository.findAll();
    }

    public PostEntity createPost(PostEntity postEntity) {
        return postRepository.createPost(postEntity);
    }

    public PostEntity findPostByNumber(int number) {
        return postRepository.findPostByNumber(number);
    }

    public void updatePost(PostEntity postEntity) {
        postRepository.updatePost(postEntity);
    }

    public void deletePost(int number) {
        postRepository.deletePost(number);
    }

}

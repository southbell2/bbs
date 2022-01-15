package com.bbs.backend.controller;

import com.bbs.backend.dto.PostDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bbs")
public class PostController {

    @Autowired
    private final PostService postService;

    @GetMapping("/posts")
    public String showPostList(Model model) {
        List<PostEntity> postEntities = postService.getPostList();
        List<PostDTO> postDTOS = postEntities.stream().map(
                postEntity -> PostDTO.builder()
                        .title(postEntity.getTitle())
                        .content(postEntity.getContent())
                        .dateTime(postEntity.getDateTime())
                        .postNumber(postEntity.getPostNumber())
                        .postViews(postEntity.getPostViews())
                        .nickname(postEntity.getNickname())
                        .build()
        ).collect(Collectors.toList());
        model.addAttribute("postList", postDTOS);

        return "/bbs/posts";
    }

}

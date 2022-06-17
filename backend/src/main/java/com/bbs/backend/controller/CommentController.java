package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.comment.CreateCommentDTO;
import com.bbs.backend.entity.CommentEntity;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.repository.CommentRepository;
import com.bbs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/comment")
@Controller
public class CommentController {

    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @Validated @RequestBody CreateCommentDTO createCommentDTO,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
    ) {
        UserEntity userInfo = userService.getUserInfo(sessionUserId);
        CommentEntity commentEntity = CommentEntity.builder()
                .username(userInfo.getUsername())
                .content(createCommentDTO.getContent())
                .postId(createCommentDTO.getPostId())
                .userId(userInfo.getId())
                .build();
        commentRepository.createComment(commentEntity);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam("post", createCommentDTO.getPostId())
                .queryParam("page", 1)
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<CommentEntity>> getComment(@RequestParam Integer post, @RequestParam(defaultValue = "1") Integer page) {
        List<CommentEntity> commentList = commentRepository.findCommentByPostId(post, page);

        return ResponseEntity.ok(commentList);
    }
}

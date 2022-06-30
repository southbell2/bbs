package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.comment.CreateCommentDTO;
import com.bbs.backend.dto.comment.GetCommentDTO;
import com.bbs.backend.entity.CommentEntity;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.exception.ForbiddenException;
import com.bbs.backend.exception.NotFoundException;
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
    public ResponseEntity<GetCommentDTO> getComment(@RequestParam Integer post, @RequestParam(defaultValue = "1") Integer page) {
        GetCommentDTO getCommentDTO = new GetCommentDTO(commentRepository.findCommentByPostId(post, page), commentRepository.allCommentNumber(post));

        return ResponseEntity.ok(getCommentDTO);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable int commentId,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
    ) {
        CommentEntity commentEntity = commentRepository.findCommentByCommentId(commentId);
        if (commentEntity == null) {
            throw new NotFoundException("댓글이 존재하지 않습니다");
        }

        System.out.println(sessionUserId);
        System.out.println(commentEntity.getUserId());
        if (!sessionUserId.equals(commentEntity.getUserId())) {
            throw new ForbiddenException("댓글 쓴 사람만 글을 삭제할 수 있습니다");
        }

        commentRepository.deleteComment(commentId);

        return ResponseEntity.ok().build();
    }
}

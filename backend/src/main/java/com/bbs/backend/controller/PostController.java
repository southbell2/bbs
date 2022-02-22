package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.post.CreatePostDTO;
import com.bbs.backend.dto.post.GetPostDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.exception.PostNotFoundException;
import com.bbs.backend.repository.PostRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bbs")
public class PostController {

    @Autowired
    private final PostRepository postRepository;

    @GetMapping("/posts")
    @ApiOperation(value = "게시글 목록 얻기", notes = "게시글 목록을 보여줍니다.")
    public List<GetPostDTO> getPostList(@RequestParam(defaultValue = "1") Integer page) {
        List<PostEntity> postEntities = postRepository.findAll();
        List<GetPostDTO> getPostDTOS = postEntities.stream()
                .map(GetPostDTO::new)
                .collect(Collectors.toList());

        return getPostDTOS;
    }

    @GetMapping("/posts/{number}")
    @ApiOperation(value = "특정 게시글 보기", notes = "게시글 번호로 특정 게시글 보기")
    public GetPostDTO getPost(@PathVariable int number) {
        PostEntity postEntity = postRepository.findPostByNumber(number);
        if (postEntity == null) {
            throw new PostNotFoundException(String.format("Post Number %s not found", number));
        }
        GetPostDTO getPostDTO = new GetPostDTO(postEntity);

        return getPostDTO;
    }

    @PostMapping("/post")
    @ApiOperation(value = "게시글 작성")
    public ResponseEntity<?> createPost(@Validated @RequestBody CreatePostDTO createPostDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postEntity.setUserId((String)session.getAttribute(SessionConst.LOGIN_USER));

        PostEntity savedPostEntity = postRepository.createPost(postEntity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("s")
                .path("/{number}")
                .buildAndExpand(savedPostEntity.getPostNumber())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{number}")
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<?> updatePost(@Validated @RequestBody CreatePostDTO createPostDTO, @PathVariable int number, HttpServletRequest request) {
        PostEntity foundPostEntity = postRepository.findPostByNumber(number);
        if (foundPostEntity == null) {
            throw new PostNotFoundException(String.format("Post Number %s not found", number));
        }

        HttpSession session = request.getSession(false);
        String sessionUserId = (String) session.getAttribute(SessionConst.LOGIN_USER);
        if (!sessionUserId.equals(foundPostEntity.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postRepository.updatePost(postEntity, number);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{number}")
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable  int number, HttpServletRequest request) {
        PostEntity foundPostEntity = postRepository.findPostByNumber(number);
        HttpSession session = request.getSession(false);
        String sessionUserId = (String) session.getAttribute(SessionConst.LOGIN_USER);
        if (!sessionUserId.equals(foundPostEntity.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        postRepository.deletePost(number);
        return ResponseEntity.ok().build();
    }

}

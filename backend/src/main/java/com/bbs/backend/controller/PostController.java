package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.post.CreatePostDTO;
import com.bbs.backend.dto.post.GetPostDTO;
import com.bbs.backend.dto.post.PageDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.exception.PostNotFoundException;
import com.bbs.backend.repository.PostRepository;
import com.bbs.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bbs")
public class PostController {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final UserService userService;

    @GetMapping("/posts")
    @ApiOperation(value = "게시글 목록 얻기")
    public PageDTO getPostList(@RequestParam(defaultValue = "1") Integer page) {
        List<PostEntity> postEntityList = postRepository.findPageByNumber(page);
        if (postEntityList.size() == 0) {
            throw new PostNotFoundException("존재하지 않는 페이지 입니다.");
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPostList(postEntityList);
        pageDTO.setAllPostNumber(postRepository.getAllPostNumber());

        return pageDTO;
    }

    @GetMapping("/posts/{number}")
    @ApiOperation(value = "특정 게시글 보기", notes = "게시글 번호로 특정 게시글 보기")
    public GetPostDTO getPost(@PathVariable int number) {
        Optional<PostEntity> postEntityOpt = checkPostExists(number);

        GetPostDTO getPostDTO = new GetPostDTO(postEntityOpt.get());

        return getPostDTO;
    }

    @PostMapping("/post")
    @ApiOperation(value = "게시글 작성")
    public ResponseEntity<?> createPost(@Validated @RequestBody CreatePostDTO createPostDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute(SessionConst.LOGIN_USER);

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postEntity.setUserId(userId);
        postEntity.setUsername(userService.getUserInfo(userId).getUsername());

        PostEntity savedPostEntity = postRepository.createPost(postEntity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("s")
                .path("/{number}")
                .buildAndExpand(savedPostEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{number}")
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<?> updatePost(
            @Validated @RequestBody CreatePostDTO createPostDTO, @PathVariable int number,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
        ) {
        Optional<PostEntity> foundPostEntityOpt = checkPostExists(number);

        if (!sessionUserId.equals(foundPostEntityOpt.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postRepository.updatePost(postEntity);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{number}")
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<?> deletePost(
            @PathVariable  int number,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
        ) {
        Optional<PostEntity> foundPostEntityOpt = checkPostExists(number);

        if (!sessionUserId.equals(foundPostEntityOpt.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postRepository.deletePost(number);
        return ResponseEntity.ok().build();
    }

    private Optional<PostEntity> checkPostExists(int number) {
        Optional<PostEntity> postEntityOpt = postRepository.findPostById(number);
        if (postEntityOpt.isEmpty()) {
            throw new PostNotFoundException(String.format("Post Number %s not found", number));
        }
        return postEntityOpt;
    }

}

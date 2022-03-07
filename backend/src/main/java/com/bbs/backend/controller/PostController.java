package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.post.CreatePostDTO;
import com.bbs.backend.dto.post.GetPostDTO;
import com.bbs.backend.dto.post.PageDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.exception.ForbiddenException;
import com.bbs.backend.exception.NotFoundException;
import com.bbs.backend.repository.PostRepository;
import com.bbs.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bbs")
public class PostController {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final UserService userService;

    @GetMapping("/posts")
    public PageDTO getPostList(@RequestParam(defaultValue = "1") Integer page) {
        List<PostEntity> postEntityList = postRepository.findPageByNumber(page);
        if (postEntityList.size() == 0) {
            throw new NotFoundException("존재하지 않는 페이지 입니다");
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPostList(postEntityList);
        pageDTO.setAllPostNumber(postRepository.getAllPostNumber());

        return pageDTO;
    }

    @GetMapping("/posts/{number}")
    @ApiOperation(value = "특정 게시글 보기", notes = "게시글 번호로 특정 게시글 보기")
    public GetPostDTO getPost(@PathVariable int number) {
        PostEntity postEntity = checkPostExists(number);

        return new GetPostDTO(postEntity);
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(
            @Validated @RequestBody CreatePostDTO createPostDTO,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
    ) {

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postEntity.setUserId(sessionUserId);
        postEntity.setUsername(userService.getUserInfo(sessionUserId).getUsername());

        PostEntity savedPostEntity = postRepository.createPost(postEntity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("s")
                .path("/{number}")
                .buildAndExpand(savedPostEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{number}")
    public ResponseEntity<?> updatePost(
            @Validated @RequestBody CreatePostDTO createPostDTO, @PathVariable int number,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
        ) {
        PostEntity foundPostEntity = checkPostExists(number);

        if (!sessionUserId.equals(foundPostEntity.getUserId())) {
            throw new ForbiddenException("글쓴사람만 글을 수정할 수 있습니다");
        }

        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postRepository.updatePost(postEntity);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{number}")
    public ResponseEntity<?> deletePost(
            @PathVariable  int number,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
        ) {
        PostEntity foundPostEntity = checkPostExists(number);

        if (!sessionUserId.equals(foundPostEntity.getUserId())) {
            throw new ForbiddenException("글쓴사람만 글을 삭제할 수 있습니다");
        }

        postRepository.deletePost(number);
        return ResponseEntity.ok().build();
    }

    private PostEntity checkPostExists(int number) {
        Optional<PostEntity> postEntityOpt = postRepository.findPostById(number);
        if (postEntityOpt.isEmpty()) {
            throw new NotFoundException("글이 존재하지 않습니다");
        }
        return postEntityOpt.get();
    }

}

package com.bbs.backend.controller;

import com.bbs.backend.dto.PostDTO;
import com.bbs.backend.dto.ResponseDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.exception.PostNotFoundException;
import com.bbs.backend.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bbs")
public class PostController {

    @Autowired
    private final PostService postService;

    @GetMapping("/posts")
    @ApiOperation(value = "게시글 목록 얻기", notes = "게시글 목록을 보여줍니다.")
    public List<PostDTO> getPostList() {
        List<PostEntity> postEntities = postService.getPostList();
        List<PostDTO> postDTOS = postEntities.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());

        return postDTOS;
    }

    @GetMapping("/posts/{number}")
    @ApiOperation(value = "특정 게시글 보기", notes = "게시글 번호로 특정 게시글 보기")
    public PostDTO getPost(@PathVariable int number) {
        PostEntity postEntity = postService.getPostByNumber(number);
        if (postEntity == null) {
            throw new PostNotFoundException(String.format("Post Number %s not found", number));
        }
        PostDTO postDTO = new PostDTO(postEntity);

        return postDTO;
    }

    @PostMapping("/post")
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성 완료시 게시글 목록으로 리다이렉트 합니다.")
    public void createPost(@RequestBody PostDTO postDTO, HttpServletResponse response) throws IOException {
        postDTO.setDateTime(LocalDateTime.now());
        postService.savePost(PostDTO.toEntity(postDTO));

        response.sendRedirect("http://localhost:8080/bbs/posts");
    }

    @PutMapping("/posts/{number}")
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<?> updatePost(@RequestBody PostDTO postDTO, @PathVariable int number) {
        PostEntity postEntity = PostDTO.toEntity(postDTO);
        postEntity.setPostNumber(number);
        postService.updatePost(postEntity);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{number}")
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable  int number) {
        postService.deletePost(number);

        return ResponseEntity.ok().build();
    }

}

package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.post.CreatePostDTO;
import com.bbs.backend.dto.post.GetPostDTO;
import com.bbs.backend.dto.post.PageDTO;
import com.bbs.backend.entity.ImageEntity;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.exception.ForbiddenException;
import com.bbs.backend.exception.NotFoundException;
import com.bbs.backend.repository.ImageRepository;
import com.bbs.backend.repository.PostRepository;
import com.bbs.backend.service.ImageService;
import com.bbs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/bbs")
@Controller
public class PostController {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ImageService imageService;

    @GetMapping("/posts")
    public ResponseEntity<PageDTO> getPostList(@RequestParam(defaultValue = "1") Integer page) {
        int allPostNumber = postRepository.getAllPostNumber();
        if (isNotPageExist(allPostNumber, page)) {
            throw new NotFoundException("존재하지 않는 페이지 입니다");
        }

        List<PostEntity> postEntityList = postRepository.findPageByNumber(page);

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPostList(postEntityList);
        pageDTO.setAllPostNumber(allPostNumber);

        return ResponseEntity.ok(pageDTO);
    }

    @GetMapping("/posts/{number}")
    public ResponseEntity<GetPostDTO> getPost(
            @PathVariable int number,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
    ){
        PostEntity postEntity = checkPostExists(number);
        boolean writer = false;
        if (sessionUserId != null && sessionUserId.equals(postEntity.getUserId())) {
            writer = true;
        }

        return ResponseEntity.ok(new GetPostDTO(postEntity, writer));
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(
            @Validated @ModelAttribute CreatePostDTO createPostDTO,
            @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String sessionUserId
    ) throws IOException {
        //게시글과 관련된 처리
        PostEntity postEntity = CreatePostDTO.toEntity(createPostDTO);
        postEntity.setUserId(sessionUserId);
        postEntity.setUsername(userService.getUserInfo(sessionUserId).getUsername());

        PostEntity savedPostEntity = postRepository.createPost(postEntity);

        //이미지 파일 처리
        if (createPostDTO.getImageFiles() != null) {
            imageService.storeImage(createPostDTO.getImageFiles(), savedPostEntity.getId());
        }

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
        postRepository.updatePost(postEntity, number);

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

        //이미지파일 삭제
        imageService.deleteImages(number);

        //글 삭제
        postRepository.deletePost(number);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(imageService.getFullPath(filename));
        MediaType contentType = MediaType.parseMediaType(Files.probeContentType(filePath));

        return ResponseEntity.ok().contentType(contentType)
                .body(new UrlResource("file:" + imageService.getFullPath(filename)));
    }

    private PostEntity checkPostExists(int number) {
        Optional<PostEntity> postEntityOpt = postRepository.findPostById(number);
        if (postEntityOpt.isEmpty()) {
            throw new NotFoundException("글이 존재하지 않습니다");
        }
        return postEntityOpt.get();
    }

    private boolean isNotPageExist(int allPostNumber, int page) {
        if (page <= 0) {
            return true;
        }
        //한 페이지당 10개의 글이 보여진다. 총 글의 갯수 보다 많은 페이지를 요구할시
        if ((allPostNumber - 1) / 10 + 1 < page) {
            return true;
        }

        return false;
    }

}

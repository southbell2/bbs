package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcPostRepositoryTest {

    @Autowired JdbcPostRepository jdbcPostRepository;

    public static String userId;
    public static int lastId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        //13개의 게시글을 만든다
        for (int i = 0; i < 13; i++) {
            PostEntity postEntity = PostEntity.builder()
                    .content("내용" + i)
                    .userId(userId)
                    .title("제목" + i)
                    .username("닉네임" + i)
                    .build();


            PostEntity post = jdbcPostRepository.createPost(postEntity);
            lastId = post.getId();
        }
    }

    @Test
    void findPageByNumber() {
        //첫번째 페이지에는 10개의 게시글이 있다
        List<PostEntity> pageByNumber1 = jdbcPostRepository.findPageByNumber(1);
        for (int i = 0; i < 10; i++) {
            assertThat(pageByNumber1.get(i).getId()).isEqualTo(lastId - i);
            assertThat(pageByNumber1.get(i).getTitle()).isEqualTo("제목" + (12 - i));
            assertThat(pageByNumber1.get(i).getUsername()).isEqualTo("닉네임" + (12 - i));
            assertThat(pageByNumber1.get(i).getViews()).isEqualTo(0);
        }

        //두번째 페이지에는 3개의 게시글이 있다
        List<PostEntity> pageByNumber2 = jdbcPostRepository.findPageByNumber(2);
        for (int i = 0; i < 3; i++) {
            assertThat(pageByNumber2.get(i).getId()).isEqualTo(lastId - 10 - i);
            assertThat(pageByNumber2.get(i).getTitle()).isEqualTo("제목" + (12 - 10 - i));
            assertThat(pageByNumber2.get(i).getUsername()).isEqualTo("닉네임" + (12 - i - 10));
            assertThat(pageByNumber2.get(i).getViews()).isEqualTo(0);
        }

        //세번째 부터는 게시글이 존재하지 않음
        List<PostEntity> pageByNumber3 = jdbcPostRepository.findPageByNumber(3);
        assertThat(pageByNumber3.size()).isEqualTo(0);
    }

    @Test
    void findPostById() {
        for (int i = 0; i < 13; i++) {
            PostEntity postById = jdbcPostRepository.findPostById(lastId - i).get();
            assertThat(postById.getId()).isEqualTo(lastId - i);
            assertThat(postById.getTitle()).isEqualTo("제목" + (12 - i));
            assertThat(postById.getUsername()).isEqualTo("닉네임" + (12 - i));
            assertThat(postById.getContent()).isEqualTo("내용" + (12 - i));
            assertThat(postById.getUserId()).isEqualTo(userId);
            //조회수가 1증가한다
            assertThat(postById.getViews()).isEqualTo(1);
        }

        //존재하지 않는 게시글
        assertThat(jdbcPostRepository.findPostById(lastId + 1).isEmpty()).isTrue();
        assertThat(jdbcPostRepository.findPostById(lastId - 13).isEmpty()).isTrue();
    }

    @Test
    void updatePost() {
        PostEntity postEntity = PostEntity.builder()
                .title("제목 수정")
                .content("내용 수정")
                .build();

        jdbcPostRepository.updatePost(postEntity, lastId);

        PostEntity postById = jdbcPostRepository.findPostById(lastId).get();
        assertThat(postById.getTitle()).isEqualTo("제목 수정");
        assertThat(postById.getContent()).isEqualTo("내용 수정");
    }

    @Test
    void getAllPostNumber() {
        assertThat(jdbcPostRepository.getAllPostNumber()).isEqualTo(13);
    }

    @Test
    void deletePost() {
        jdbcPostRepository.deletePost(lastId);
        Optional<PostEntity> postById = jdbcPostRepository.findPostById(lastId);
        assertThat(postById.isEmpty()).isTrue();
    }
}
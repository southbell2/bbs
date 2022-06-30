package com.bbs.backend.repository;

import com.bbs.backend.entity.CommentEntity;
import com.bbs.backend.entity.PostEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class JdbcCommentRepositoryTest {

    @Autowired JdbcCommentRepository jdbcCommentRepository;
    @Autowired JdbcPostRepository jdbcPostRepository;

    public static String userId;
    public static int postId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        //글 1개를 만든다
        PostEntity postEntity = PostEntity.builder()
                .content("내용")
                .userId(userId)
                .title("제목")
                .username("닉네임")
                .build();

        PostEntity post = jdbcPostRepository.createPost(postEntity);
        postId = post.getId();

        //생성한 글에 댓글 11개를 만든다
        for (int i = 1; i <= 11; i++) {
            CommentEntity commentEntity = CommentEntity.builder()
                    .username("홍길동" + i)
                    .content("댓글 테스트" + i)
                    .postId(postId)
                    .userId(userId)
                    .build();

            jdbcCommentRepository.createComment(commentEntity);
        }

    }

    @Test
    void findCommentByPostId() {
        //댓글의 첫번째 페이지에는 총 10개의 댓글이 보여야 한다
        List<CommentEntity> comments = jdbcCommentRepository.findCommentByPostId(postId, 1);
        assertThat(comments.size()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            int idx = i + 2;
            assertThat(comments.get(i).getUsername()).isEqualTo("홍길동" + idx);
            assertThat(comments.get(i).getContent()).isEqualTo("댓글 테스트" + idx);
        }

        comments = jdbcCommentRepository.findCommentByPostId(postId, 2);
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getUsername()).isEqualTo("홍길동1");
        assertThat(comments.get(0).getContent()).isEqualTo("댓글 테스트1");
    }

    @Test
    void deleteComment() {
        //11개의 댓글 중 가장 마지막 댓글을 삭제한다
        List<CommentEntity> comments = jdbcCommentRepository.findCommentByPostId(postId, 1);
        int commentId = comments.get(9).getId();
        jdbcCommentRepository.deleteComment(commentId);

        //다시 댓글 얻기, 총 댓글 1~10 까지 존재
        comments = jdbcCommentRepository.findCommentByPostId(postId, 1);
        for (int i = 0; i < comments.size(); i++) {
            int idx = i + 1;
            assertThat(comments.get(i).getUsername()).isEqualTo("홍길동" + idx);
            assertThat(comments.get(i).getContent()).isEqualTo("댓글 테스트" + idx);
        }
    }

    @Test
    void findCommentByCommentId() {
        //11개의 댓글 중 가장 마지막 댓글의 id를 얻는다
        List<CommentEntity> comments = jdbcCommentRepository.findCommentByPostId(postId, 1);
        int commentId = comments.get(9).getId();

        CommentEntity commentEntity = jdbcCommentRepository.findCommentByCommentId(commentId);
        assertThat(commentEntity.getUsername()).isEqualTo("홍길동11");
        assertThat(commentEntity.getContent()).isEqualTo("댓글 테스트11");
    }

}
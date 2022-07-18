package com.bbs.backend.repository;

import com.bbs.backend.entity.ImageEntity;
import com.bbs.backend.entity.PostEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.config.location=classpath:application.yml,classpath:aws.yml"})
@Transactional
class JdbcImageRepositoryTest {

    @Autowired JdbcImageRepository jdbcImageRepository;
    @Autowired JdbcPostRepository jdbcPostRepository;

    @BeforeEach
    void setUp() {
        PostEntity postEntity1 = jdbcPostRepository.createPost(PostEntity.builder()
                .title("test title")
                .content("test content")
                .username("james")
                .userId("test")
                .build()
        );
        PostEntity postEntity2 = jdbcPostRepository.createPost(PostEntity.builder()
                .title("test title")
                .content("test content")
                .username("james")
                .userId("test2")
                .build()
        );


        List<ImageEntity> imageEntities = new ArrayList<>();
        imageEntities.add(new ImageEntity(postEntity1.getId(), UUID.randomUUID().toString() + ".jpg"));
        imageEntities.add(new ImageEntity(postEntity2.getId(), UUID.randomUUID().toString() + ".png"));
        imageEntities.add(new ImageEntity(postEntity2.getId(), UUID.randomUUID().toString() + ".gif"));

        jdbcImageRepository.saveImage(imageEntities);
    }

    @Test
    void findByPostId() {
        //포스트 아이디 1번
        List<ImageEntity> imageEntities = jdbcImageRepository.findByPostId(1);
        for (ImageEntity imageEntity : imageEntities) {
            assertThat(imageEntity.getPostId()).isEqualTo(1);
            int lastIdx = imageEntity.getFilename().lastIndexOf(".");
            assertThat(imageEntity.getFilename().substring(lastIdx + 1)).isEqualTo("jpg");
        }

        //포스트 아이디 2번
        imageEntities = jdbcImageRepository.findByPostId(2);
        for (ImageEntity imageEntity : imageEntities) {
            assertThat(imageEntity.getPostId()).isEqualTo(2);
            int lastIdx = imageEntity.getFilename().lastIndexOf(".");
            String ext = imageEntity.getFilename().substring(lastIdx + 1);
            assertThat(ext.equals("png") || ext.equals("gif")).isTrue();
        }

        //포스트 아이디 3번 (존재 X)
        imageEntities = jdbcImageRepository.findByPostId(3);
        assertThat(imageEntities).isEmpty();
    }
}
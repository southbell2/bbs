package com.bbs.backend.service;

import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.repository.JdbcUserRepository;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@SpringBootTest(properties = {"spring.config.location=classpath:application.yml,classpath:aws.yml"})
@Transactional
class UserServiceTest {

    @Autowired UserService userService;

    public static String savedUUID;

    @BeforeEach
    public void setUp() {
        //각 테스틀 진행하기 전에 회원을 등록
        savedUUID = UUID.randomUUID().toString();
        UserEntity userEntity = UserEntity.builder()
                .email("test@email.com")
                .password("test1234")
                .username("test1234")
                .id(savedUUID)
                .build();

        userService.saveUser(userEntity);
    }

    @Test
    void checkExistEmail() {
        assertThat(userService.checkExistEmail("test@email.com")).isTrue();
        assertThat(userService.checkExistEmail("test2@eamil.com")).isFalse();
    }

    @Test
    void checkExistUsername() {
        assertThat(userService.checkExistUsername("test1234")).isTrue();
        assertThat(userService.checkExistUsername("test12")).isFalse();
    }

    @Test
    void login() {
        //존재하는 회원은 로그인 성공
        Optional<UserEntity> testUser1 = userService.login("test@email.com", "test1234");
        assertThat(testUser1.isPresent()).isTrue();

        //이메일은 맞지만 비밀번호는 틀린 경우
        Optional<UserEntity> testUser2 = userService.login("test@email.com", "test123");
        assertThat(testUser2.isEmpty()).isTrue();

        //비밀번호는 맞지만 이메일은 틀린 경우
        Optional<UserEntity> testUser3 = userService.login("test3@email.com", "test1234");
        assertThat(testUser3.isEmpty()).isTrue();

        //이메일 비밀번호 둘다 틀린 경우
        Optional<UserEntity> testUser4 = userService.login("test4@email.com", "test12345");
        assertThat(testUser4.isEmpty()).isTrue();
    }

}
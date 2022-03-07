package com.bbs.backend.repository;

import com.bbs.backend.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class JdbcUserRepositoryTest {

    @Autowired JdbcUserRepository jdbcUserRepository;

    public static String savedUUID;

    @BeforeEach
    void setUp() {
        //각 테스틀 진행하기 전에 회원을 등록
        savedUUID = UUID.randomUUID().toString();
        UserEntity userEntity = UserEntity.builder()
                .email("test@email.com")
                .password("test1234")
                .username("test1234")
                .id(savedUUID)
                .build();

        jdbcUserRepository.saveUser(userEntity);
    }

    @Test
    void saveUser() {
        //똑같은 id 로는 가입이 될 수 없음을 테스트
        UserEntity testUser = UserEntity.builder()
                .email("test2@email.com")
                .password("test2")
                .username("test2")
                .id(savedUUID)
                .build();
        Assertions.assertThrows(DuplicateKeyException.class, () -> jdbcUserRepository.saveUser(testUser));
    }

    @Test
    void deleteUser() {
        jdbcUserRepository.deleteUser(savedUUID);
        Optional<UserEntity> foundUser = jdbcUserRepository.findById(savedUUID);
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    void findById() {
        //존재하는 회원 찾기
        UserEntity foundUser = jdbcUserRepository.findById(savedUUID).get();
        assertThat(foundUser.getId()).isEqualTo(savedUUID);
        assertThat(foundUser.getUsername()).isEqualTo("test1234");
        assertThat(foundUser.getJoinDate()).isEqualTo(LocalDate.now().toString());
        assertThat(foundUser.getPassword()).isEqualTo("test1234");
        assertThat(foundUser.getEmail()).isEqualTo("test@email.com");

        //존재하지 않는 회원 찾기
        Optional<UserEntity> userOpt = jdbcUserRepository.findById("asdf-1234");
        assertThat(userOpt.isEmpty()).isTrue();
    }

    @Test
    void findByEmail() {
        //존재하는 회원 찾기
        UserEntity foundUser = jdbcUserRepository.findByEmail("test@email.com").get();
        assertThat(foundUser.getId()).isEqualTo(savedUUID);
        assertThat(foundUser.getUsername()).isEqualTo("test1234");
        assertThat(foundUser.getJoinDate()).isEqualTo(LocalDate.now().toString());
        assertThat(foundUser.getPassword()).isEqualTo("test1234");
        assertThat(foundUser.getEmail()).isEqualTo("test@email.com");

        //존재하지 않는 회원 찾기
        Optional<UserEntity> userOpt = jdbcUserRepository.findByEmail("test2@email.com");
        assertThat(userOpt.isEmpty()).isTrue();
    }

    @Test
    void findByUsername() {
        //존재하는 회원 찾기
        UserEntity foundUser = jdbcUserRepository.findByUsername("test1234").get();
        assertThat(foundUser.getId()).isEqualTo(savedUUID);
        assertThat(foundUser.getUsername()).isEqualTo("test1234");
        assertThat(foundUser.getJoinDate()).isEqualTo(LocalDate.now().toString());
        assertThat(foundUser.getPassword()).isEqualTo("test1234");
        assertThat(foundUser.getEmail()).isEqualTo("test@email.com");

        //존재하지 않는 회원 찾기
        Optional<UserEntity> userOpt = jdbcUserRepository.findByEmail("test123456");
        assertThat(userOpt.isEmpty()).isTrue();
    }
}
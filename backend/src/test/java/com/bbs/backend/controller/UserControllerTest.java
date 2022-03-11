package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.ExceptionDTO;
import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.dto.user.UserInfoDTO;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.interceptor.UserLoginCheckInterceptor;
import com.bbs.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    public static String id;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        id = UUID.randomUUID().toString();
        UserEntity userEntity = UserEntity.builder()
                .id(id)
                .username("james")
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        userService.saveUser(userEntity);
    }

    @Test
    void createUser() throws Exception {
        //이메일이 중복된 경우
        UserEntity userEntity1 = UserEntity.builder()
                .email("spring@test.com")
                .username("kane")
                .password("asdf1234")
                .build();

        String url = "/user";
        MvcResult mvcResult = mockMvc.perform(
                post(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEntity1))
        ).andExpect(status().isBadRequest()).andReturn();

        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");


        //username이 중복된 경우
        UserEntity userEntity2 = UserEntity.builder()
                .email("spring2@test.com")
                .username("james")
                .password("asdf1234")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEntity2))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("이미 존재하는 닉네임입니다.");


        //이메일을 입력하지 않은 경우
        UserEntity userEntity3 = UserEntity.builder()
                .username("john")
                .password("asdf1234")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity3))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("이메일을 입력하세요");

        //이메일 형식이 아닌 경우
        UserEntity userEntity4 = UserEntity.builder()
                .username("john")
                .password("asdf1234")
                .email("testnaver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity4))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("이메일 형식이 아닙니다");

        //username을 입려하지 않은 경우
        UserEntity userEntity5 = UserEntity.builder()
                .password("asdf1234")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity5))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("닉네임을 입력하세요");

        //username의 범위 2~10을 벗어난 경우
        UserEntity userEntity6 = UserEntity.builder()
                .username("a")
                .password("asdf1234")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity6))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("닉네임의 크기는 2~10 사이여야 합니다");

        UserEntity userEntity7 = UserEntity.builder()
                .username("abcde123456")
                .password("asdf1234")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity7))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("닉네임의 크기는 2~10 사이여야 합니다");


        //비밀번호를 입력하지 않은 경우
        UserEntity userEntity8 = UserEntity.builder()
                .username("tom")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity8))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("비밀번호를 입력하세요");


        //비밀번호의 범위 4~15를 벗어난 경우
        UserEntity userEntity9 = UserEntity.builder()
                .username("tom")
                .password("asd")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity9))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("비밀번호의 크기는 4~15 사이여야 합니다");

        UserEntity userEntity10 = UserEntity.builder()
                .username("tom")
                .password("asddfk345343##$d")
                .email("test@naver.com")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userEntity10))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("비밀번호의 크기는 4~15 사이여야 합니다");
    }

    @Test
    void login() throws Exception {
        //로그인 성공한 경우
        LoginDTO loginDTO = LoginDTO.builder()
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        String url = "/user/login";
        mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk());

        //로그인 실패한 경우 - 이메일이 틀린 경우
        LoginDTO loginDTO2 = LoginDTO.builder()
                .email("spring2@test.com")
                .password("qwer1234")
                .build();

        MvcResult mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO2))
        ).andExpect(status().isBadRequest()).andReturn();

        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("아이디 혹은 비밀번호가 일치하지 않습니다");


        ////로그인 실패한 경우 - 비밀번호가 틀린 경우
        LoginDTO loginDTO3 = LoginDTO.builder()
                .email("spring@test.com")
                .password("wer1234")
                .build();

        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO3))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("아이디 혹은 비밀번호가 일치하지 않습니다");
    }

    @Test
    void logout() throws Exception {
        //로그인
        LoginDTO loginDTO = LoginDTO.builder()
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        String url = "/user/login";
        MvcResult mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();

        MockHttpSession session = (MockHttpSession)mvcResult.getRequest().getSession(false);

        //로그인 후 유저정보 얻기
        url = "/user/yourAccount";
        mvcResult = mockMvc.perform(get(url).session(session)).andExpect(status().isOk()).andReturn();
        UserInfoDTO userInfoDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserInfoDTO.class);
        assertThat(userInfoDTO.getUsername()).isEqualTo("james");
        assertThat(userInfoDTO.getEmail()).isEqualTo("spring@test.com");
        assertThat(userInfoDTO.getPassword()).isEqualTo("qwer1234");
        assertThat(userInfoDTO.getJoinDate()).isEqualTo(LocalDate.now());

        //로그아웃 하기
        url = "/user/logout";
        mockMvc.perform(post(url).session(session)).andExpect(status().isOk());

        //로그아웃하고 유저 정보를 얻기
        url = "/user/yourAccount";
        mvcResult = mockMvc.perform(get(url)).andExpect(status().isUnauthorized()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("로그인을 해주세요");
    }

    @Test
    void deleteUser() throws Exception{
        //로그인하지 않고 계정 삭제 테스트
        String url = "/user/yourAccount";
        MvcResult mvcResult = mockMvc.perform(delete(url)).andExpect(status().isUnauthorized()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("로그인을 해주세요");

        //로그인 후 계정 삭제
        //로그인
        LoginDTO loginDTO = LoginDTO.builder()
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();

        MockHttpSession session = (MockHttpSession)mvcResult.getRequest().getSession(false);

        //계정 삭제
        url = "/user/yourAccount";
        mockMvc.perform(delete(url).session(session)).andExpect(status().isOk()).andReturn();

        //다시 로그인 해보기
        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isBadRequest()).andReturn();

        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("아이디 혹은 비밀번호가 일치하지 않습니다");
    }
}
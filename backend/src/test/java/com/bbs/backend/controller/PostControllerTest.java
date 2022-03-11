package com.bbs.backend.controller;

import com.bbs.backend.dto.ExceptionDTO;
import com.bbs.backend.dto.post.CreatePostDTO;
import com.bbs.backend.dto.post.GetPostDTO;
import com.bbs.backend.dto.post.PageDTO;
import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.repository.JdbcPostRepository;
import com.bbs.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JdbcPostRepository jdbcPostRepository;
    @Autowired
    UserService userService;

    public static String userId;
    public int lastId;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        userId = UUID.randomUUID().toString();
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .username("james")
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        userService.saveUser(userEntity);

        //13개의 글 작성
        for (int i = 0; i < 13; i++) {
            PostEntity postEntity = PostEntity.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .username("james")
                    .userId(userId)
                    .build();

            lastId = jdbcPostRepository.createPost(postEntity).getId();
        }

        userEntity = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username("john")
                .email("spring2@test.com")
                .password("qwer1234")
                .build();

        userService.saveUser(userEntity);
    }

    @Test
    void getPostList() throws Exception {
        //첫번째 페이지
        String url = "/bbs/posts";
        MvcResult mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isOk()).andReturn();
        PageDTO pageDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PageDTO.class);
        assertThat(pageDTO.getAllPostNumber()).isEqualTo(13);
        for (int i = 0; i < 10; i++) {
            assertThat(pageDTO.getPostList().get(i).getUsername()).isEqualTo("james");
            assertThat(pageDTO.getPostList().get(i).getTitle()).isEqualTo("제목" + (12 - i));
            assertThat(pageDTO.getPostList().get(i).getViews()).isEqualTo(0);
        }

        //두번째 페이지
        url = "/bbs/posts?page=2";
        mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isOk()).andReturn();
        pageDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PageDTO.class);
        assertThat(pageDTO.getAllPostNumber()).isEqualTo(13);
        for (int i = 0; i < 3; i++) {
            assertThat(pageDTO.getPostList().get(i).getUsername()).isEqualTo("james");
            assertThat(pageDTO.getPostList().get(i).getTitle()).isEqualTo("제목" + (2 - i));
            assertThat(pageDTO.getPostList().get(i).getViews()).isEqualTo(0);
        }

        //세번째 페이지
        url = "/bbs/posts?page=3";
        mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isNotFound()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("존재하지 않는 페이지 입니다");
    }

    @Test
    void getPost() throws Exception {
        //가장 마지막에 쓰여진 글 조회
        String url = "/bbs/posts/" + lastId;
        MvcResult mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isOk()).andReturn();

        GetPostDTO getPostDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GetPostDTO.class);
        assertThat(getPostDTO.getContent()).isEqualTo("내용" + 12);
        assertThat(getPostDTO.getTitle()).isEqualTo("제목" + 12);
        assertThat(getPostDTO.getUsername()).isEqualTo("james");
        assertThat(getPostDTO.getViews()).isEqualTo(1);
        assertThat(getPostDTO.getId()).isEqualTo(lastId);

        //존재하지 않는 글 조회
        url = "/bbs/posts/1";
        mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isNotFound()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("글이 존재하지 않습니다");
    }

    @Test
    void createPost() throws Exception {
        //로그인 하지 않고 글쓰기
        String url = "/bbs/post";
        MvcResult mvcResult = mockMvc.perform(
                post(url)
        ).andExpect(status().isUnauthorized()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("로그인을 해주세요");

        //로그인 하고 글쓰기
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
        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession();

        //정상적인 글쓰기
        CreatePostDTO createPostDTO = new CreatePostDTO("제목 테스트", "내용 테스트");
        url = "/bbs/post";
        mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .session(session)
        ).andExpect(status().isCreated()).andExpect(header().stringValues("location", "http://localhost/bbs/posts/" + (lastId + 1))).andReturn();

        //검증에 맞지 않는 경우
        createPostDTO = new CreatePostDTO("", "내용 테스트");
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .session(session)
        ).andExpect(status().isBadRequest()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("제목을 입력하세요");

        createPostDTO = new CreatePostDTO("제목 테스트", "");
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .session(session)
        ).andExpect(status().isBadRequest()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("내용을 입력하세요");

        createPostDTO = new CreatePostDTO("012345678901234567890123456789012345678901234567890", "내용 테스트");
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createPostDTO))
                        .session(session)
        ).andExpect(status().isBadRequest()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("제목은 50자를 넘으면 안 됩니다");
    }

    @Test
    void updatePost() throws Exception {
        //로그인 하지 않고 글 수정
        CreatePostDTO createPostDTO = new CreatePostDTO("제목 수정", "내용 수정");

        String url = "/bbs/posts/" + lastId;
        MvcResult mvcResult = mockMvc.perform(
                put(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDTO))
        ).andExpect(status().isUnauthorized()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("로그인을 해주세요");

        //다른 사용자가 글 수정
        //로그인
        LoginDTO loginDTO = LoginDTO.builder()
                .email("spring2@test.com")
                .password("qwer1234")
                .build();

        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);

        url = "/bbs/posts/" + lastId;
        mvcResult = mockMvc.perform(
                put(url).session(session).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDTO))
        ).andExpect(status().isForbidden()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("글쓴사람만 글을 수정할 수 있습니다");

        //글쓴사람이 수정
        loginDTO = LoginDTO.builder()
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();
        session = (MockHttpSession) mvcResult.getRequest().getSession(false);

        url = "/bbs/posts/" + lastId;
        mockMvc.perform(
                put(url).session(session).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDTO))
                ).andExpect(status().isOk());

        //수정된 글 확인
        mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isOk()).andReturn();
        GetPostDTO getPostDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GetPostDTO.class);
        assertThat(getPostDTO.getTitle()).isEqualTo("제목 수정");
        assertThat(getPostDTO.getContent()).isEqualTo("내용 수정");
    }

    @Test
    void deletePost() throws Exception {
        //로그인 하지 않고 글 삭제
        String url = "/bbs/posts/" + lastId;
        MvcResult mvcResult = mockMvc.perform(
                delete(url)
        ).andExpect(status().isUnauthorized()).andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("로그인을 해주세요");

        //다른 사용자가 글 삭제
        //로그인
        LoginDTO loginDTO = LoginDTO.builder()
                .email("spring2@test.com")
                .password("qwer1234")
                .build();
        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);

        url = "/bbs/posts/" + lastId;
        mvcResult = mockMvc.perform(
                delete(url).session(session)
        ).andExpect(status().isForbidden()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("글쓴사람만 글을 삭제할 수 있습니다");

        //글쓴사람이 삭제
        loginDTO = LoginDTO.builder()
                .email("spring@test.com")
                .password("qwer1234")
                .build();

        url = "/user/login";
        mvcResult = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andExpect(status().isOk()).andReturn();
        session = (MockHttpSession) mvcResult.getRequest().getSession(false);

        url = "/bbs/posts/" + lastId;
        mockMvc.perform(
                delete(url).session(session)
        ).andExpect(status().isOk());

        //삭제한 글 보기
        url = "/bbs/posts/" + lastId;
        mvcResult = mockMvc.perform(
                get(url)
        ).andExpect(status().isNotFound()).andReturn();
        exceptionDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDTO.class);
        assertThat(exceptionDTO.getMessage()).isEqualTo("글이 존재하지 않습니다");
    }
}
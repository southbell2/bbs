package com.bbs.backend.service;

import com.bbs.backend.entity.PostEntity;
import com.bbs.backend.repository.JdbcPostRepository;
import com.bbs.backend.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ImageServiceTest {

    @Autowired ImageService imageService;
    @Autowired JdbcPostRepository jdbcPostRepository;

    public static List<MultipartFile> multipartFileList = new ArrayList<>();
    public static PostEntity postEntity;

    @BeforeEach
    void setUp() throws Exception {
        postEntity = jdbcPostRepository.createPost(PostEntity.builder()
                .title("test title")
                .content("test content")
                .username("james")
                .userId("test")
                .build()
        );

        FileInputStream fis = new FileInputStream("C:\\Users\\User\\testimage\\pizza.jpg");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "pizza.jpg", "image/jpeg", fis);
        multipartFileList.add(mockMultipartFile);
    }

    @Test
    void getFullPath() {
        String filename = "pizza.jpg";
        String fullPath = imageService.getFullPath(filename);
        assertThat(fullPath).isEqualTo("C:\\Users\\User\\Desktop\\imagefolder\\pizza.jpg");
    }

    @Test
    void storeImage() throws IOException {
        //이미지가 저장된 위치 얻기
        List<String> storeImageName = imageService.storeImage(multipartFileList, postEntity.getId());
        String storeImageFullPath = imageService.getFullPath(storeImageName.get(0));

        //저장된 이미지의 FileInputStream
        FileInputStream savedFis = new FileInputStream(storeImageFullPath);
        int savedByte;

        //MultipartFile에 담겨 있는 원본 이미지 파일의 InputStream
        InputStream originalIs = multipartFileList.get(0).getInputStream();

        //저장된 이미지와 원본 이미지를 byte 단위로 비교
        while ((savedByte = savedFis.read()) != -1) {
            assertThat(savedByte).isEqualTo(originalIs.read());
        }
        savedFis.close();
        originalIs.close();

        //저장된 파일 삭제
        File savedFile = new File(storeImageFullPath);
        assertThat(savedFile.delete()).isTrue();
    }
}
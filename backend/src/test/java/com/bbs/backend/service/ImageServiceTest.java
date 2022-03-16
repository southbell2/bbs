package com.bbs.backend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ImageServiceTest {

    @Autowired ImageService imageService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getFullPath() {
        String filename = "pizza.jpg";
        String fullPath = imageService.getFullPath(filename);
        assertThat(fullPath).isEqualTo("C:\\Users\\User\\Desktop\\imagefolder\\pizza.jpg");
    }

    @Test
    void storeImage() {

    }
}
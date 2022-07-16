package com.bbs.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    public String getFullPath(String filename);

    public List<String> storeImage(List<MultipartFile> imageFiles, int postId) throws IOException;

    public void deleteImages(int postId);
}

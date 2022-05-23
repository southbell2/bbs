package com.bbs.backend.service;

import com.bbs.backend.entity.ImageEntity;
import com.bbs.backend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.dir}")
    private String fileDir;

    @Autowired
    ImageRepository imageRepository;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<String> storeImage(List<MultipartFile> imageFiles, int postId) throws IOException {
        List<ImageEntity> imageEntityList = new ArrayList<>();
        List<String> storedFileNameList = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                String storeFileName = createStoreFileName(imageFile.getOriginalFilename());
                storedFileNameList.add(storeFileName);
                imageFile.transferTo(new File(getFullPath(storeFileName)));
                imageEntityList.add(new ImageEntity(postId, storeFileName));
            }
        }

        imageRepository.saveImage(imageEntityList);

        return storedFileNameList;
    }

    public List<ImageEntity> findByPostId(int postId) {
        return imageRepository.findByPostId(postId);
    }

    public void deleteImages(int postId) {
        List<ImageEntity> imageEntities = findByPostId(postId);
        for (ImageEntity imageEntity : imageEntities) {
            String fullPath = getFullPath(imageEntity.getFilename());
            File savedImageFile = new File(fullPath);
            savedImageFile.delete();
        }
    }

    private String createStoreFileName(String originalFileName) {
        int idx = originalFileName.lastIndexOf(".");
        return UUID.randomUUID().toString() + originalFileName.substring(idx);
    }

}

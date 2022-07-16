package com.bbs.backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bbs.backend.config.AmazonS3Config;
import com.bbs.backend.entity.ImageEntity;
import com.bbs.backend.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageService implements ImageService{

    private final AmazonS3Client amazonS3Client;

    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    @Override
    public String getFullPath(String filename) {
        return amazonS3Client.getUrl(bucket, filename).toString();
    }

    @Override
    public List<String> storeImage(List<MultipartFile> imageFiles, int postId) throws IOException {
        List<ImageEntity> imageEntityList = new ArrayList<>();
        List<String> fullPathUrls = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                //저장될 파일이름을 UUID로 생성
                String storeFileName = createStoreFileName(imageFile.getOriginalFilename());
                //파일을 잠시 저장하고 s3에 올린 후 다시 삭제
                File convertedFile = new File(System.getProperty("user.dir") + "/" + storeFileName);
                imageFile.transferTo(convertedFile);
                amazonS3Client.putObject(new PutObjectRequest(bucket, storeFileName, convertedFile).withCannedAcl(CannedAccessControlList.PublicRead));
                convertedFile.delete();
                //s3에 저장된 이미지의 객체 url을 저장한다
                String fullPathUrl = getFullPath(storeFileName);
                fullPathUrls.add(fullPathUrl);
                imageEntityList.add(new ImageEntity(postId, fullPathUrl));
            }
        }

        imageRepository.saveImage(imageEntityList);

        return fullPathUrls;
    }

    @Override
    public void deleteImages(int postId) {

    }

    private String createStoreFileName(String originalFileName) {
        int idx = originalFileName.lastIndexOf(".");
        return UUID.randomUUID().toString() + originalFileName.substring(idx);
    }
}

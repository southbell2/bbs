package com.bbs.backend.repository;

import com.bbs.backend.entity.ImageEntity;

import java.util.List;

public interface ImageRepository {
    void saveImage(List<ImageEntity> imageEntities);
    List<ImageEntity> findByPostId(int postId);
    void deleteImageByPostId(int postId);
}

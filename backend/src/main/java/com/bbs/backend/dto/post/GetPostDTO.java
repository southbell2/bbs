package com.bbs.backend.dto.post;

import com.bbs.backend.entity.ImageEntity;
import com.bbs.backend.entity.PostEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDTO {

    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int id;
    private int views;
    private boolean writer;
    private List<String> imageFiles = new ArrayList<>();

    public GetPostDTO(final PostEntity postEntity, boolean writer) {
        this.username = postEntity.getUsername();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.createdAt = postEntity.getCreatedAt();
        this.id = postEntity.getId();
        this.views = postEntity.getViews();
        this.writer = writer;
        for (String imageFileName : postEntity.getImageFileNames()) {
            imageFiles.add(imageFileName);
        }
    }

}

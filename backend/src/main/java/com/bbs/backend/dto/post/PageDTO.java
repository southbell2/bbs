package com.bbs.backend.dto.post;

import com.bbs.backend.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    private List<PostForPage> postList;
    private int allPostNumber;

    public void setPostList(List<PostEntity> postEntityList) {
        this.postList = postEntityList.stream().map(PostForPage::new).collect(Collectors.toList());
    }

    @Getter
    public static class PostForPage {
        private int id;
        private String title;
        private String username;
        private LocalDateTime createdAt;
        private int views;

        public PostForPage(final PostEntity postEntity) {
            this.id = postEntity.getId();
            this.title = postEntity.getTitle();
            this.username = postEntity.getUsername();
            this.createdAt = postEntity.getCreatedAt();
            this.views = postEntity.getViews();
        }
    }
}

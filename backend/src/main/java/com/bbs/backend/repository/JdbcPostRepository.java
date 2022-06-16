package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class JdbcPostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPostRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<PostEntity> findPageByNumber(int number) {
        return jdbcTemplate.query("SELECT id, title, username, created_at, views FROM posts ORDER BY id DESC LIMIT ?, 10",
                pageRowMapper(), (number-1) * 10);
    }

    @Override
    public PostEntity createPost(PostEntity postEntity) {
        jdbcTemplate.update("INSERT INTO posts (username, content, title, user_id) VALUES(?, ?, ?, ?)",
                postEntity.getUsername(), postEntity.getContent(), postEntity.getTitle(), postEntity.getUserId()
                );
        int id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", (rs, rowNum) -> rs.getInt(1));
        postEntity.setId(id);

        return postEntity;
    }

    @Override
    public Optional<PostEntity> findPostById(int id) {
        List<PostEntity> postEntity =  jdbcTemplate.query("SELECT * FROM posts LEFT JOIN images ON posts.id = images.post_id WHERE posts.id = ?", (rs) -> {
            PostEntity tempPostEntity = null;
            while (rs.next()) {
                int postId = rs.getInt("id");
                String username = rs.getString("username");
                String title = rs.getString("title");
                String content = rs.getString("content");
                LocalDateTime created_at = rs.getTimestamp("created_at").toLocalDateTime();
                int views = rs.getInt("views");
                String user_id = rs.getString("user_id");
                String filename = rs.getString("filename");

                if (tempPostEntity == null) {
                    tempPostEntity = PostEntity.builder()
                            .id(postId)
                            .username(username)
                            .title(title)
                            .content(content)
                            .createdAt(created_at)
                            .views(views)
                            .userId(user_id)
                            .imageFileNames(new ArrayList<>())
                            .build();
                }

                if (filename != null) {
                    tempPostEntity.getImageFileNames().add(filename);
                }

            }

            List<PostEntity> list = new ArrayList<>();
            if (tempPostEntity != null) {
                list.add(tempPostEntity);
            }

            return list;
        }, id);

        Optional<PostEntity> returnOpt = Optional.empty();
        //글이 존재하면 조회수 1 증가
        if(!postEntity.isEmpty()) {
            int views = postEntity.get(0).getViews();
            jdbcTemplate.update("UPDATE posts SET views=? WHERE id=?",
                    ++views, postEntity.get(0).getId()
            );
            postEntity.get(0).setViews(views);

            returnOpt = Optional.ofNullable(postEntity.get(0));
        }

        return returnOpt;
    }

    @Override
    public void updatePost(PostEntity postEntity, int id) {
        jdbcTemplate.update("UPDATE posts SET title=?, content=? WHERE id=?",
                postEntity.getTitle(), postEntity.getContent(), id
                );
    }

    @Override
    public int getAllPostNumber() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts", (rs, rowNum) -> rs.getInt(1));
    }

    @Override
    public void deletePost(int id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id=?", id);
    }

    private RowMapper<PostEntity> pageRowMapper() {
        return (rs, rowNum) -> {
            PostEntity postEntity = new PostEntity();
            postEntity.setUsername(rs.getString("username"));
            postEntity.setTitle(rs.getString("title"));
            postEntity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            postEntity.setId(rs.getInt("id"));
            postEntity.setViews(rs.getInt("views"));

            return postEntity;
        };
    }
}

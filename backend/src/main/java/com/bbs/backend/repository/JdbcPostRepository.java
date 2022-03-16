package com.bbs.backend.repository;

import com.bbs.backend.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


@Repository
public class JdbcPostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPostRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
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
        PostEntity postEntity = null;
        try {
            postEntity = jdbcTemplate.queryForObject("SELECT * FROM posts WHERE id=?", postRowMapper(), id);
        } catch (EmptyResultDataAccessException ignored) {}

        //조회수 1 증가
        if(postEntity != null) {
            int views = postEntity.getViews();
            jdbcTemplate.update("UPDATE posts SET views=? WHERE id=?",
                    ++views, postEntity.getId()
            );
            postEntity.setViews(views);
        }

        return Optional.ofNullable(postEntity);
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

    private RowMapper<PostEntity> postRowMapper() {
        return (rs, rowNum) -> {
            PostEntity postEntity = new PostEntity();
            postEntity.setUsername(rs.getString("username"));
            postEntity.setTitle(rs.getString("title"));
            postEntity.setContent(rs.getString("content"));
            postEntity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            postEntity.setId(rs.getInt("id"));
            postEntity.setViews(rs.getInt("views"));
            postEntity.setUserId(rs.getString("user_id"));

            return postEntity;
        };
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

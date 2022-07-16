package com.bbs.backend.repository;

import com.bbs.backend.entity.CommentEntity;
import com.bbs.backend.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JdbcCommentRepository implements CommentRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createComment(CommentEntity commentEntity) {
        jdbcTemplate.update(
                "INSERT INTO comments (username, content, post_id, user_id) VALUES(?, ?, ?, ?)",
                commentEntity.getUsername(), commentEntity.getContent(), commentEntity.getPostId(), commentEntity.getUserId()
        );
    }

    @Override
    public List<CommentEntity> findCommentByPostId(int postId, int commentPageNumber) {
        return jdbcTemplate.query("SELECT id, username, content, created_at, post_id FROM comments WHERE post_id = ? ORDER BY id DESC LIMIT ?, 10",
                commentRowMapper(), postId, (commentPageNumber-1) * 10).stream().sorted((c1, c2) -> c1.getId() - c2.getId()).collect(Collectors.toList());
    }

    @Override
    public CommentEntity findCommentByCommentId(int commentId) {
        CommentEntity commentEntity = null;
        try {
            commentEntity = jdbcTemplate.queryForObject("SELECT * FROM comments WHERE id=?", fullCommentRowMapper(), commentId);
        } catch(EmptyResultDataAccessException ignored) {}

        return commentEntity;
    }

    @Override
    public void deleteComment(int commentId) {
        jdbcTemplate.update("DELETE FROM comments WHERE id=?", commentId);
    }

    @Override
    public int allCommentNumber(int postId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE post_id = ?", (rs, rowNum) -> rs.getInt(1), postId);
    }

    private RowMapper<CommentEntity> commentRowMapper() {
        return (rs, rowNum) -> {
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setId(rs.getInt("id" ));
            commentEntity.setUsername(rs.getString("username"));
            commentEntity.setContent(rs.getString("content"));
            commentEntity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            commentEntity.setPostId(rs.getInt("post_id"));

            return commentEntity;
        };
    }

    private RowMapper<CommentEntity> fullCommentRowMapper() {
        return (rs, rowNum) -> {
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setId(rs.getInt("id" ));
            commentEntity.setUsername(rs.getString("username"));
            commentEntity.setContent(rs.getString("content"));
            commentEntity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            commentEntity.setPostId(rs.getInt("post_id"));
            commentEntity.setUserId(rs.getString("user_id"));

            return commentEntity;
        };
    }
}

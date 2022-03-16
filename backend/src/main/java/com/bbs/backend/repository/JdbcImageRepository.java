package com.bbs.backend.repository;

import com.bbs.backend.entity.ImageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class JdbcImageRepository implements ImageRepository{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcImageRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveImage(List<ImageEntity> imageEntities) {
        for (ImageEntity imageEntity : imageEntities) {
            jdbcTemplate.update("INSERT INTO images (post_id, filename) VALUES (?, ?)",
            imageEntity.getPostId(), imageEntity.getFilename());
        }
    }

    @Override
    public List<ImageEntity> findByPostId(int postId) {
        return jdbcTemplate.query("SELECT * FROM images WHERE post_id=?", imageRowMapper(), postId);
    }

    private RowMapper<ImageEntity> imageRowMapper() {
        return (rs, rowNum) -> {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setFilename(rs.getString("filename"));
            imageEntity.setPostId(rs.getInt("post_id"));

            return imageEntity;
        };
    }
}

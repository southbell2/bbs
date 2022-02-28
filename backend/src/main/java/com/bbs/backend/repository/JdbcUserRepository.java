package com.bbs.backend.repository;

import com.bbs.backend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcUserRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        jdbcTemplate.update("INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)",
                    userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), userEntity.getEmail()
                );
        return userEntity;
    }

    @Override
    public void deleteUser(String id) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        UserEntity userEntity = null;
        try {
            userEntity = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", userRowMapper(), id);
        } catch(EmptyResultDataAccessException ignored) {}

        return Optional.ofNullable(userEntity);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        UserEntity userEntity = null;
        try {
            userEntity = jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", userRowMapper(), email);
        } catch (EmptyResultDataAccessException ignored) {}

        return Optional.ofNullable(userEntity);
    }

    private RowMapper<UserEntity> userRowMapper() {
        return (rs, rowNum) -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime().toLocalDate());
            userEntity.setId(rs.getString("id"));
            userEntity.setUsername(rs.getString("username"));
            userEntity.setEmail(rs.getString("email"));
            userEntity.setPassword(rs.getString("password"));

            return userEntity;
        };
    }

}

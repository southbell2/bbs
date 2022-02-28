package com.bbs.backend.repository;

import com.bbs.backend.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserEntity saveUser(UserEntity user);

    void deleteUser(String id);

    Optional<UserEntity> findById(String id);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);
}

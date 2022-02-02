package com.bbs.backend.repository;

import com.bbs.backend.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class UserMemoryRepository implements UserRepository {
    private Map<String, UserEntity> store = new HashMap<>();

    @Override
    public UserEntity saveUser(UserEntity user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setJoinDate(LocalDate.now());
        store.put(id, user);

        return user;
    }

    @Override
    public void deleteUser(String id) {
        store.remove(id);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<UserEntity> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }
}

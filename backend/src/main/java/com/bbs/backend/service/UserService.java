package com.bbs.backend.service;

import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.saveUser(userEntity);
    }

    public boolean checkExistEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkExistUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void deleteUser(String id) {
        userRepository.deleteUser(id);
    }

    public Optional<UserEntity> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public UserEntity getUserInfo(String id) {
        return userRepository.findById(id).get();
    }

}

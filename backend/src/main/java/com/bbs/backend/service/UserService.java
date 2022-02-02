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

    /**
     * 회원 가입에 성공하면 true 를 반환한다.
     * 가입하려고 하는 이메일이 이미 존재할 시에는 false 를 반환한다.
     */
    public boolean saveUser(UserEntity userEntity) {
        Optional<UserEntity> foundUserByEmail = userRepository.findByEmail(userEntity.getEmail());
        if (foundUserByEmail.isPresent()) {
            return false;
        } else {
            userRepository.saveUser(userEntity);
            return true;
        }
    }

    public void deleteUser(String id) {
        userRepository.deleteUser(id);
    }

    public Optional<UserEntity> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }


}

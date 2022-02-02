package com.bbs.backend.controller;

import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.dto.user.UserDTO;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Validated @RequestBody UserDTO userDTO) {
        UserEntity userEntity = UserDTO.toEntity(userDTO);
        if (userService.saveUser(userEntity)) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/yourAccount")
                    .build()
                    .toUri();
            return ResponseEntity.created(location).build();
        } else {
            //예외 처리는 나중에
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO) {
        Optional<UserEntity> loginOpt = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        if (loginOpt.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}

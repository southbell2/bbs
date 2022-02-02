package com.bbs.backend.controller;

import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.dto.user.UserDTO;
import com.bbs.backend.dto.user.UserInfoDTO;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public static final String LOGIN_USER = "loginUser";

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
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        Optional<UserEntity> loginOpt = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        if (loginOpt.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute(LOGIN_USER, loginOpt.get().getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/yourAccount")
    public ResponseEntity<UserInfoDTO> getUserInfo(@SessionAttribute(name = LOGIN_USER, required = false) String id) {
        UserInfoDTO userInfoDTO = new UserInfoDTO(userService.getUserInfo(id));

        return ResponseEntity.ok(userInfoDTO);
    }

    @DeleteMapping("/yourAccount")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String id = (String)session.getAttribute(LOGIN_USER);
        userService.deleteUser(id);
        session.invalidate();

        return ResponseEntity.ok().build();
    }

}

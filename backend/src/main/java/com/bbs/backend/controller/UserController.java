package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.dto.user.UserDTO;
import com.bbs.backend.dto.user.UserInfoDTO;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.exception.UserNotFoundException;
import com.bbs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Validated @RequestBody UserDTO userDTO) {
        UserEntity userEntity = UserDTO.toEntity(userDTO);
        userEntity.setId(UUID.randomUUID().toString());

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
            session.setAttribute(SessionConst.LOGIN_USER, loginOpt.get().getId());
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
    public ResponseEntity<UserInfoDTO> getUserInfo(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String id) {
        UserEntity user = userService.getUserInfo(id);
        if (user == null) {
            throw new UserNotFoundException("User doesn't exist");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO(user);

        return ResponseEntity.ok(userInfoDTO);
    }

    @DeleteMapping("/yourAccount")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String id = (String)session.getAttribute(SessionConst.LOGIN_USER);
        userService.deleteUser(id);
        session.invalidate();

        return ResponseEntity.ok().build();
    }

}

package com.bbs.backend.controller;

import com.bbs.backend.SessionConst;
import com.bbs.backend.dto.user.LoginDTO;
import com.bbs.backend.dto.user.UserDTO;
import com.bbs.backend.dto.user.UserInfoDTO;
import com.bbs.backend.entity.UserEntity;
import com.bbs.backend.exception.NotFoundException;
import com.bbs.backend.exception.BadRequestException;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Validated @RequestBody UserDTO userDTO) {
        UserEntity userEntity = UserDTO.toEntity(userDTO);
        if (userService.checkExistEmail(userEntity.getEmail())) {
            throw new BadRequestException("이미 존재하는 이메일입니다.");
        }

        if (userService.checkExistUsername(userEntity.getUsername())) {
            throw new BadRequestException("이미 존재하는 닉네임입니다.");
        }

        userEntity.setId(UUID.randomUUID().toString());
        userService.saveUser(userEntity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/yourAccount")
                .build()
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        Optional<UserEntity> loginOpt = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        if (loginOpt.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_USER, loginOpt.get().getId());
            return ResponseEntity.ok().build();
        } else {
            throw new BadRequestException("아이디 혹은 비밀번호가 일치하지 않습니다");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/yourAccount")
    public ResponseEntity<UserInfoDTO> getUserInfo(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String id) {
        UserEntity user = userService.getUserInfo(id);
        if (user == null) {
            throw new NotFoundException("존재하지 않는 회원입니다");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO(user);

        return ResponseEntity.ok(userInfoDTO);
    }

    @DeleteMapping("/yourAccount")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String id = (String)session.getAttribute(SessionConst.LOGIN_USER);
        userService.deleteUser(id);
        session.invalidate();

        return ResponseEntity.ok().build();
    }

}

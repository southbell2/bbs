package com.bbs.backend.dto.user;

import com.bbs.backend.entity.UserEntity;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 4, max = 15, message = "비밀번호의 크기는 4~15 사이여야 합니다")
    private String password;

    @Size(min = 2, max = 10, message = "닉네임의 크기는 2~10 사이여야 합니다")
    @NotBlank(message = "닉네임을 입력하세요")
    private String username;

    public static UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .username(userDTO.getUsername())
                .build();
    }
}

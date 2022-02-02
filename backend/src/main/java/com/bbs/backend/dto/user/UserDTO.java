package com.bbs.backend.dto.user;

import com.bbs.backend.entity.UserEntity;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 15)
    private String password;

    @Size(min = 2, max = 10)
    @NotBlank
    private String nickname;

    public static UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .nickname(userDTO.getNickname())
                .build();
    }
}

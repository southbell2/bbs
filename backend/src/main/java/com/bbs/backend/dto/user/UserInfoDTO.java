package com.bbs.backend.dto.user;

import com.bbs.backend.entity.UserEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoDTO {
    private String email;
    private String nickname;
    private String password;
    private LocalDate joinDate;

    public UserInfoDTO(UserEntity userEntity) {
        this.email = userEntity.getEmail();
        this.nickname = userEntity.getNickname();
        this.password = userEntity.getPassword();
        this.joinDate = userEntity.getJoinDate();
    }
}

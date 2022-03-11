package com.bbs.backend.dto.user;

import com.bbs.backend.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String email;
    private String username;
    private String password;
    private LocalDate joinDate;

    public UserInfoDTO(UserEntity userEntity) {
        this.email = userEntity.getEmail();
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.joinDate = userEntity.getJoinDate();
    }
}

package com.jp.parkapi.web.dto;

import com.jp.parkapi.entities.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateDTO {

    private String username;
    private String password;

}

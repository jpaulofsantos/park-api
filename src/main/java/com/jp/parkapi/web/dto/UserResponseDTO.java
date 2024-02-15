package com.jp.parkapi.web.dto;

import com.jp.parkapi.entities.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {

    private Long id;
    private String username;
    private String role;

    public UserResponseDTO(User user){
        id = user.getId();
        username = user.getUsername();
        role = user.getRole().name();
    }
}

package com.jp.parkapi.web.dto;

import com.jp.parkapi.entities.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateDTO {

    @NotBlank
    @Email(message = "Formato do email inválido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    private String username;

    @NotBlank
    @Size(min = 6, max = 6)
    private String password;

}

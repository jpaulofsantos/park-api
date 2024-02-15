package com.jp.parkapi.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPasswordDTO {

    private String senhaAtual;
    private String novaSenha;
    private String confirmaSenha;
}

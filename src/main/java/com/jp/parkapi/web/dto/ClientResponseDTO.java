package com.jp.parkapi.web.dto;

import com.jp.parkapi.entities.Client;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClientResponseDTO {

    private Long id;

    private String name;

    private String cpf;

    public ClientResponseDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.cpf = client.getCpf();
    }
}

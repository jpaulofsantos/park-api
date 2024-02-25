package com.jp.parkapi.web.dto.mapper;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.web.dto.ClientCreateDTO;
import com.jp.parkapi.web.dto.ClientResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    public static Client toClient(ClientCreateDTO clientCreateDTO) {
        return new ModelMapper().map(clientCreateDTO, Client.class);
    }

    public static ClientResponseDTO toDto(Client client) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(client, ClientResponseDTO.class);
    }

    public static Page<ClientResponseDTO> toClientPage(Page<Client> clientPage) {
        return clientPage.map(client -> toDto(client));
    }
}

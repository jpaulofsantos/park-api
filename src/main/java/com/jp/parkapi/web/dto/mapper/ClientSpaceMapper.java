package com.jp.parkapi.web.dto.mapper;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.web.dto.ParkingCreateDTO;
import com.jp.parkapi.web.dto.ParkingResponseDTO;
import com.jp.parkapi.web.dto.ParkingSpaceResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientSpaceMapper {

    public static ClientSpace toClientSpace(ParkingCreateDTO parkingCreateDTO) {
        return new ModelMapper().map(parkingCreateDTO, ClientSpace.class);
    }

    public static ParkingResponseDTO toDto(ClientSpace clientSpace) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(clientSpace, ParkingResponseDTO.class);
    }

    public static Page<ParkingResponseDTO> toParkingPage(Page<ClientSpace> clientSpacePage) {
        return clientSpacePage.map(clienteSpace -> toDto(clienteSpace));
    }

    public static Page<ParkingResponseDTO> toDtoPage(Page<ClientSpace> clientSpacePage) {
        return clientSpacePage.map(space -> toDto(space));
    }

}

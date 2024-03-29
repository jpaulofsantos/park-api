package com.jp.parkapi.web.dto.mapper;

import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.entities.User;
import com.jp.parkapi.web.dto.ParkingSpaceCreateDTO;
import com.jp.parkapi.web.dto.ParkingSpaceResponseDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.matcher.StringMatcher;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpaceMapper {

    public static ParkingSpace toParkingSpace(ParkingSpaceCreateDTO parkingSpaceCreateDTO) {
        return new ModelMapper().map(parkingSpaceCreateDTO, ParkingSpace.class);
    }

    public static ParkingSpaceResponseDTO toDto(ParkingSpace parkingSpace) {
        return new ModelMapper().map(parkingSpace, ParkingSpaceResponseDTO.class);
    }

    public static Page<ParkingSpaceResponseDTO> toDtoPage(Page<ParkingSpace> parkingSpacePage) {
        return parkingSpacePage.map(space -> toDto(space));
    }
}

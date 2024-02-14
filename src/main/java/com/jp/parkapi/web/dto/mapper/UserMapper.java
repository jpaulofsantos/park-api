package com.jp.parkapi.web.dto.mapper;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class UserMapper {

    public static User toUser(UserCreateDTO userCreateDTO) {
        return new ModelMapper().map(userCreateDTO, User.class);
    }

    public static UserResponseDTO toDto(User user) {
        String role = user.getRole().name().substring("ROLE_".length());
        PropertyMap<User, UserResponseDTO> props = new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        ModelMapper modelMapper =  new ModelMapper();
        modelMapper.addMappings(props);

        return modelMapper.map(user, UserResponseDTO.class);
    }
}

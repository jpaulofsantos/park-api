package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.services.UserService;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserPasswordDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> insertUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User result = userService.insertUser(UserMapper.toUser(userCreateDTO));
        /*User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(userCreateDTO.getPassword());
        userService.insertUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user));*/
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(result));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        User result = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(result));
    }

    @PatchMapping(value = "/{id}") //atualização parcial (PUT -> atualização total, porpem pode usar o PUT para atualização parcial também)
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO userPasswordDTO) { //senha será enviada no corpo da requisição e não como parâmetro da url
        User result = (userService.updatePassword(id, userPasswordDTO.getSenhaAtual(), userPasswordDTO.getNovaSenha(), userPasswordDTO.getConfirmaSenha()));
        return ResponseEntity.noContent().build(); //retornando No content, pois nesse caso não é necessário retornar o response dto com os valores
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(Pageable pageable) {
        Page<User> result = userService.findAllUsersPaged(pageable);
        return ResponseEntity.ok(UserMapper.toDtoPage(result));
    }
}

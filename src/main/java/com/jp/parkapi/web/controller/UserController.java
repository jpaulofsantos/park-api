package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.services.UserService;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.dto.mapper.UserMapper;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<UserResponseDTO> insertUser(@RequestBody UserCreateDTO userCreateDTO) {
        User result = userService.insertUser(UserMapper.toUser(userCreateDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(result));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        User result = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(result));
    }

    @PatchMapping(value = "/{id}") //atualização parcial (PUT -> atualização total, porpem pode usar o PUT para atualização parcial também)
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody User user) { //senha será enviada no corpo da requisição e não como parâmetro da url
        return ResponseEntity.ok(userService.updatePassword(id, user.getPassword()));
    }

    @GetMapping
    public ResponseEntity<Page<User>> findAllUsers(Pageable pageable) {
        Page<User> result = userService.findAllUsersPaged(pageable);
        return ResponseEntity.ok(result);
    }
}

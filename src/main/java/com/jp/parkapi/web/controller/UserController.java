package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.User;
import com.jp.parkapi.services.UserService;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserPasswordDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.dto.mapper.UserMapper;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Contém as operações relativas aos recursos de cadastro, inserção e leitura de um usuário.")
@RestController
@RequestMapping(value = "api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usário", responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Usuário/email já cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
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

    @Operation(summary = "Recuperar um usuário pelo ID", description = "Recurso para recuperar um usuário pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENT') AND #id == authentication.principal.id)") //user admin autenticado consegue chamar esse metodo e ver os demais ids. Ja o CLIENT somente o proprio id.
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        User result = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(result));
    }

    @Operation(summary = "Atualizar senha", description = "Recurso para atualização de senha", responses = {
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "400", description = "Senha não confere",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos ou mal formatados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping(value = "/{id}") //atualização parcial (PUT -> atualização total, porpem pode usar o PUT para atualização parcial também)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO userPasswordDTO) { //senha será enviada no corpo da requisição e não como parâmetro da url
        User result = (userService.updatePassword(id, userPasswordDTO.getSenhaAtual(), userPasswordDTO.getNovaSenha(), userPasswordDTO.getConfirmaSenha()));
        return ResponseEntity.noContent().build(); //retornando No content, pois nesse caso não é necessário retornar o response dto com os valores
    }

    @Operation(summary = "Recuperar todos os usuários com paginação", description = "Recurso para recuperar todos os usuários com paginação", responses = {
            @ApiResponse(responseCode = "200", description = "Recursos recuperados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(Pageable pageable) {
        Page<User> result = userService.findAllUsersPaged(pageable);
        return ResponseEntity.ok(UserMapper.toDtoPage(result));
    }
}

package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.jwt.JwtUserDetails;
import com.jp.parkapi.services.ClientService;
import com.jp.parkapi.services.UserService;
import com.jp.parkapi.web.dto.ClientCreateDTO;
import com.jp.parkapi.web.dto.ClientResponseDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.dto.mapper.ClientMapper;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clients", description = "Contém as operações relativas aos recursos de cadastro, inserção e leitura de um cliente.")
@RestController
@RequestMapping(value = "api/v1/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    UserService userService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente", responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Cliente/CPF já cadastrado no sistema",
                    content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                    content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                    content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> insertClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(clientCreateDTO); //convertendo para Client, recebendo um clientCreateDTO
        client.setUser(userService.findById(userDetails.getId())); //vinculando o usuário a partir do contexto do Spring Security
        clientService.insertClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientMapper.toDto(client)); //convertendo para ClientResponseDTO, recebendo um Client
    }

    @Operation(summary = "Recurso para recuperar um cliente pelo ID", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            responses = {
            @ApiResponse(responseCode = "200", description = "Cliente recuperado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil CLIENTE",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id){
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }
}

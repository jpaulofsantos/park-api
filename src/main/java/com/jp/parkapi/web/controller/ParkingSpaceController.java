package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.services.ParkingSpaceService;
import com.jp.parkapi.web.dto.ParkingSpaceCreateDTO;
import com.jp.parkapi.web.dto.ParkingSpaceResponseDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.dto.mapper.ParkingSpaceMapper;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking Spaces", description = "Contém as operações relativas aos recursos de cadastro, inserção e leitura de vagas.")
@RestController
@RequestMapping(value = "api/v1/parkingspaces")
public class ParkingSpaceController {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @Operation(summary = "Criar uma nova vaga", description = "Recurso para criar uma nova vaga. Requisição exige um Bearer Token. Acesso restrito a ADMIN ",
            security = @SecurityRequirement(name = "security"),
            responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criada")),
            @ApiResponse(responseCode = "409", description = "Vaga já cadastrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> insertParkingSpace(@Valid @RequestBody ParkingSpaceCreateDTO parkingSpaceCreateDTO) { //alterando a forma de rertorno (diferente do retorno usando o responseDTO
        ParkingSpace parkingSpace = parkingSpaceService.insertSpace(ParkingSpaceMapper.toParkingSpace(parkingSpaceCreateDTO));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(parkingSpace.getCode())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpaceResponseDTO> findSpaceByCode(@PathVariable String code) {
        ParkingSpace parkingSpace = parkingSpaceService.findByCode(code);
        return ResponseEntity.ok(ParkingSpaceMapper.toDto(parkingSpace));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ParkingSpaceResponseDTO>> findAllPaged(Pageable pageable) {
        Page<ParkingSpace> parkingSpacePage = parkingSpaceService.findAllPaged(pageable);
        return ResponseEntity.ok(ParkingSpaceMapper.toDtoPage(parkingSpacePage));
    }
}

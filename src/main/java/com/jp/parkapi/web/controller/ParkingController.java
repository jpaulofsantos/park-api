package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.services.ParkingService;
import com.jp.parkapi.web.dto.ParkingCreateDTO;
import com.jp.parkapi.web.dto.ParkingResponseDTO;
import com.jp.parkapi.web.dto.mapper.ClientMapper;
import com.jp.parkapi.web.dto.mapper.ClientSpaceMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Tag(name = "Parkings", description = "Contém as operações de registros de entrada e saída de um veículo no estacionamento.")
@RestController
@RequestMapping("api/v1/parkings")
public class ParkingController {

    @Autowired
    ParkingService parkingService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dare entrada em um veículo no estacionamento. Requisição exige um Bearer Token. Acesso restrito a ADMIN ",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criada"),
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Causas possíveis: <br/> " +
                            "- CPF do cliente não cadastrado no sistema; <br/> " +
                            "- Nenhuma vaga livre foi localizada;",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil CLIENTE",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkIn(@RequestBody @Valid ParkingCreateDTO parkingCreateDTO) {
        ClientSpace clientSpace = ClientSpaceMapper.toClientSpace(parkingCreateDTO);
        parkingService.checkIn(clientSpace);

        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(clientSpace.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(parkingResponseDTO);
    }

    @GetMapping(value = "/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingResponseDTO> findByReceipt(@PathVariable String receipt) {
        ClientSpace clientSpace = parkingService.findCheckInByCode(receipt);
        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);
        return ResponseEntity.ok().body(parkingResponseDTO);
    }

    @Operation(summary = "Localizar um veículo estacionado", description = "Recurso para retornar um veículo estacionado " +
            "pelo nº do recibo. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "recibo", description = "Número do rebibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping(value = "/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkOut(@PathVariable String receipt) {
        ClientSpace clientSpace = parkingService.checkOut(receipt);
        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);
        return ResponseEntity.ok().body(parkingResponseDTO);
    }
}
